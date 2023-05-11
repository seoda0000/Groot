package com.chocobi.groot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.utils.doOnApplyWindowInsets
import io.github.sceneview.utils.setFullScreen

class CharacterActivity : AppCompatActivity(R.layout.activity_character) {
    private val TAG = "CharacterActivity"
    lateinit var sceneView: ArSceneView

    private lateinit var GLBfile: String
    private lateinit var level: String
    private lateinit var potName: String
    private lateinit var potPlant: String

    //    lateinit var loadingView: View
//    private lateinit var statusText: TextView

    //    lateinit var placeModelButton: ExtendedFloatingActionButton
//    lateinit var newModelButton: ExtendedFloatingActionButton
    lateinit var changeAnimationButton: ExtendedFloatingActionButton
//    lateinit var stopAnimationButton: ExtendedFloatingActionButton
//    lateinit var resumeAnimationButton: ExtendedFloatingActionButton
    private var animationIdx = 0
//    private var time = System.currentTimeMillis()

    data class Model(
        val fileLocation: String,
        val scaleUnits: Float? = null,
        val placementMode: PlacementMode? = PlacementMode.INSTANT,
        val applyPoseRotation: Boolean? = true
    )

    val models =
        Model(
            fileLocation = "https://groot-a303-s3.s3.ap-northeast-2.amazonaws.com/assets/tree_2.glb",
//            placementMode = PlacementMode.INSTANT,
//            applyPoseRotation = false
        )

    var modelNode: ArModelNode? = null
        //    var isLoading = false
        set(value) {
            field = value
//            loadingView.isGone = !value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_character)

        GLBfile = intent.getStringExtra("GLBfile").toString()
        level = intent.getStringExtra("level").toString()
        potName = intent.getStringExtra("potName").toString()
        potPlant = intent.getStringExtra("potPlant").toString()

        animationIdx = if (level == "0") 9 else 0

        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

//        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar)?.apply {
//            doOnApplyWindowInsets { systemBarsInsets ->
//                (layoutParams as ViewGroup.MarginLayoutParams).topMargin = systemBarsInsets.top
//            }
//            title = ""
//        })
//        statusText = findViewById(R.id.statusText)
//        statusText.text = potName
        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            onArTrackingFailureChanged = { reason ->
                Toast.makeText(context, "사물을 감지하지 못해 메인 화면으로 돌아갑니다", Toast.LENGTH_LONG).show()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
//                statusText.text = reason?.getDescription(context)
//                statusText.isGone = reason == null
            }
            isDepthOcclusionEnabled = false
        }
//        loadingView = findViewById(R.id.loadingView)
//        newModelButton = findViewById<ExtendedFloatingActionButton>(R.id.newModelButton).apply {
//            // Add system bar margins
//        }
//        placeModelButton = findViewById<ExtendedFloatingActionButton>(R.id.placeModelButton).apply {
//            setOnClickListener { placeModelNode() }
//        }
        changeAnimationButton = findViewById(R.id.changeAnimation)
        changeAnimationButton.apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { changeAnimation() }

        }

//        stopAnimationButton = findViewById(R.id.stopAnimation)
//        stopAnimationButton.apply {
//            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
//            doOnApplyWindowInsets { systemBarsInsets ->
//                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
//                    systemBarsInsets.bottom + bottomMargin
//            }
//            setOnClickListener { stopAnimtion() }
//
//        }
//
//        resumeAnimationButton = findViewById(R.id.resumeAnimation)
//        resumeAnimationButton.setOnClickListener {
//            resumeAnimation()
//        }

        newModelNode()
//        placeModelNode()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.a, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    fun changeAnimation() {
        modelNode?.stopAnimation(animationIdx)
        val maxIdx = modelNode?.animator?.animationCount!!
        ++animationIdx
        if (animationIdx == maxIdx) {
            animationIdx = if (level == "0") 9 else 0
        }
//        modelNode?.animator?.playbackSpeed =0.5f
        modelNode?.playAnimation(animationIdx)
//        val duration = modelNode?.animator?.getAnimationDuration(animationIdx)
//        val elapsedTimeInSeconds = duration?.times(2f) ?: 0f
//        modelNode?.animator?.applyAnimation(animationIdx, duration?.times(1f)!!)
//        Log.d("CharacterActivity", "changeAnimation() ${modelNode?.animator}")
//        Log.d("CharacterActivity", "changeAnimation() ${elapsedTimeInSeconds} ${duration}")
    }

    fun stopAnimtion() {
        modelNode?.stopAnimation(animationIdx)
    }

    fun resumeAnimation() {
        modelNode?.playAnimation(animationIdx)
    }

//    fun placeModelNode() {
//        Log.d(TAG, "placeModelNode()")
//        modelNode?.anchor()
//        placeModelButton.isVisible = false
//        sceneView.planeRenderer.isVisible = false
//    }

    fun newModelNode() {
//        isLoading = true
        modelNode?.takeIf { !it.isAnchored }?.let {
            sceneView.removeChild(it)
            it.destroy()
        }
        val model = models
//        modelIndex = (modelIndex + 1) % models.size
        modelNode = ArModelNode(
            placementMode = PlacementMode.INSTANT,
            instantAnchor = true,
            followHitPosition = false,

            ).apply {
//            applyPoseRotation = model.applyPoseRotation
            loadModelGlbAsync(
                glbFileLocation = GLBfile ?: model.fileLocation,
                autoAnimate = false,
                scaleToUnits = 0.2f,
                centerOrigin = Position(x = 0f, y = 0.0f, z = 0f)
                // Place the model origin at the bottom center
            ) {
                isPositionEditable = true
                isScaleEditable = false
                isRotationEditable = true
                followHitPosition = false
                instantAnchor = true

//                applyPosePosition = false

//                isScaleEditable = false
//            playAnimation(0, true)
            }
//            position = Position(x = 0.0f, y = 0f, z = 10f)
//            rotation = Rotation(x=10.0f, y = -10f, z=0f)
//            scale = Scale(1f)
            setReceiveShadows(false)
            setCastShadows(false)
            setScreenSpaceContactShadows(false)

//            onAnchorChanged = { anchor ->
//                placeModelButton.isGone = anchor != null
//            }
//            onHitResult = { node, _ ->
//                placeModelButton.isGone = !node.isTracking
//            }
        }
        modelNode?.anchor()


        sceneView.addChild(modelNode!!)
        // Select the model node by default (the model node is also selected on tap)
        sceneView.selectedNode = modelNode

    }
}