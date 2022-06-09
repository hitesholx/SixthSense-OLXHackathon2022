package com.demo.sixthsense

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import com.google.ar.core.*
import com.google.ar.sceneform.*
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.lang.Exception
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.abs
import com.demo.sixthsense.MainActivity.MyGestureDetector
import java.io.File
import android.content.Intent




class MainActivity : AppCompatActivity(), ControlFragment.OnQuaternionChangedListener,
    Scene.OnUpdateListener, Node.OnTouchListener {
    private lateinit var gestureListener: View.OnTouchListener
    private lateinit var gestureDetector: GestureDetector
    private var node: TransformableNode? = null
    private lateinit var modelRenderable: ModelRenderable
    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = (supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment?)!!
        gestureDetector = GestureDetector(this, MyGestureDetector())
        gestureListener = View.OnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }

        findViewById<View>(R.id.main_view).setOnTouchListener(gestureListener)
        setUpModel()
    }
    private fun setUpModel() {
        val fileName = "ferrari.glb"
        val modelUri = Uri.parse("android.resource://com.demo.sixthsense/raw/ferrari")
        ModelRenderable.builder()
            .setSource(
                this,
                RenderableSource.builder().setSource(
                    this,
                    modelUri,
                    RenderableSource.SourceType.GLB)
                    .setScale(0.75f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId(fileName)
            .build()
            .thenAccept(Consumer { renderable: ModelRenderable ->
                modelRenderable = renderable
                arFragment.arSceneView.scene.addOnUpdateListener(this@MainActivity)
            })
            .exceptionally(Function<Throwable, Void?> { throwable: Throwable? ->
                Log.i("Model", "cant load")
                Toast.makeText(this@MainActivity, "Model can't be Loaded", Toast.LENGTH_SHORT)
                    .show()
                null
            })
    }

    override fun onLeft(value: Float) {
        node?.apply {
            Log.d("left", value.toString())
            localRotation = Quaternion.axisAngle(
                Vector3(0.0f, 1.0f, 0.0f),
                value
            )
        }
    }

    override fun onRight(value: Float) {
        node?.apply {
            Log.d("right", value.toString())
            localRotation = Quaternion.axisAngle(
                Vector3(0.0f, 1.0f, 0.0f),
                -value
            )
        }
    }

    override fun onTop(value: Float) {
        node?.apply {
            Log.d("right", value.toString())
            localRotation = Quaternion.axisAngle(
                Vector3(0.0f, 0.0f, 1.0f),
                value
            )
        }
    }

    override fun onBottom(value: Float) {
        node?.apply {
            Log.d("right", value.toString())
            localRotation = Quaternion.axisAngle(
                Vector3(0.0f, 0.0f, 1.0f),
                -value
            )
        }
    }

    override fun onUpdate(frameTime: FrameTime?) {
        if (node != null) {
            return
        }
        //get the frame from the scene for shorthand
        val frame = arFragment.arSceneView.arFrame
        if (frame != null) {
            //get the trackables to ensure planes are detected
            val var3 = frame.getUpdatedTrackables(Plane::class.java).iterator()
            while(var3.hasNext()) {
                val plane = var3.next() as Plane

                //If a plane has been detected & is being tracked by ARCore
                if (plane.trackingState == TrackingState.TRACKING) {

                    //Hide the plane discovery helper animation
                    arFragment.planeDiscoveryController.hide()


                    //Get all added anchors to the frame
                    val iterableAnchor = frame.updatedAnchors.iterator()

                    //place the first object only if no previous anchors were added
                    if(!iterableAnchor.hasNext()) {
                        //Perform a hit test at the center of the screen to place an object without tapping
                        val hitTest = frame.hitTest(frame.screenCenter().x, frame.screenCenter().y)

                        //iterate through all hits
                        val hitTestIterator = hitTest.iterator()
                        while(hitTestIterator.hasNext()) {
                            val hitResult = hitTestIterator.next()

                            //Create an anchor at the plane hit
                            val modelAnchor = plane.createAnchor(hitResult.hitPose)

                            //Attach a node to this anchor with the scene as the parent
                            val anchorNode = AnchorNode(modelAnchor)
                            anchorNode.setParent(arFragment.arSceneView.scene)

                            //create a new TranformableNode that will carry our object
                            node = TransformableNode(arFragment.transformationSystem)
                            node?.rotationController?.isEnabled = true
                            node?.scaleController?.isEnabled = true
                            node?.translationController?.isEnabled = true
                            node?.setParent(anchorNode)
                            node?.renderable = this@MainActivity.modelRenderable

                            //Alter the real world position to ensure object renders on the table top. Not somewhere inside.
                            node?.worldPosition = Vector3(modelAnchor.pose.tx(),
                                modelAnchor.pose.compose(Pose.makeTranslation(0f, 0.05f, 0f)).ty(),
                                modelAnchor.pose.tz())
                            return
                        }
                    }
                }
            }
        }
    }

    inner class MyGestureDetector: GestureDetector.SimpleOnGestureListener() {
        private var value1: Float = 10F
        private var value2: Float = 10F

        private val SWIPE_MIN_DISTANCE = 120
        private val SWIPE_MAX_OFF_PATH = 250
        private val SWIPE_THRESHOLD_VELOCITY = 200
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                if (abs(e1!!.y - e2!!.y) > SWIPE_MAX_OFF_PATH) return false
                // right to left swipe
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    value1 =+50f
                    onLeft(value1)
                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    value2 =+50f
                    onRight(value2)
                }
            } catch (e: Exception) {
                // nothing
            }
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }

    private fun Frame.screenCenter(): Vector3 {
        val vw = findViewById<View>(android.R.id.content)
        return Vector3(vw.width / 2f, vw.height / 2f, 0f)
    }

    override fun onTouch(p0: HitTestResult?, event: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }
}