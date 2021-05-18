package tw.edu.pu.s410855443.crazyshape

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import org.tensorflow.lite.support.image.TensorImage
import tw.edu.pu.s410855443.crazyshape.ml.Shapes

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        btnBack.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                finish()
            }
        })

        btn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                handPaint.path.reset()
                handPaint.invalidate()
            }
        })

        handPaint.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(p0: View?, event: MotionEvent): Boolean {
                var xPos = event.getX()
                var yPos = event.getY()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> handPaint.path.moveTo(xPos, yPos)
                    MotionEvent.ACTION_MOVE -> handPaint.path.lineTo(xPos, yPos)
                    MotionEvent.ACTION_UP -> {

                        val b = Bitmap.createBitmap(handPaint.measuredWidth, handPaint.measuredHeight,
                            Bitmap.Config.ARGB_8888)
                        val c = Canvas(b)
                        handPaint.draw(c)
                        classifyDrawing(b)
                    }
                }
                handPaint.invalidate()
                return true
            }
        })
    }


    fun classifyDrawing(bitmap : Bitmap) {
        val model = Shapes.newInstance(this)


        val image = TensorImage.fromBitmap(bitmap)


        val outputs = model.process(image)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }.take(1)
        var Result:String = ""
        var FlagDraw:Int = 0
        when (outputs[0].label) {
            "circle" -> {Result = "圓形"
                FlagDraw=1}
            "square" -> {Result = "方形"
                FlagDraw=2}
            "star" -> {Result = "星形"
                FlagDraw=3}
            "triangle" -> {Result = "三角形"
                FlagDraw=4}
        }
        Result += ": " + String.format("%.1f%%", outputs[0].score * 100.0f)



        model.close()
        Toast.makeText(this, Result, Toast.LENGTH_SHORT).show()
    }

}
