package com.example.verificationcodedemo

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.captchaedit.view.*
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class CaptchaEdit(context: Context, attrs: AttributeSet?) : RelativeLayout(context,attrs) {
    private val mRelativeLayouts = ArrayList<RelativeLayout>()
    private val inputBox = ArrayList<TextView>()
    private val cursorBox = ArrayList<View>()
    private lateinit var alphaAnimation : AlphaAnimation

    public interface InputListener{
        public fun finishInput()
    }

    companion object{
        private var inputstr = StringBuilder("")    //输入的内容
        private var isSet = false   //是否已经初始化过
        private var etCount = 6             //验证码输入框数量
        private var etWidth = 150           //验证码输入框宽度
        //private var etHeight = 150          //验证码输入框高度
        private var etBgColor = Color.parseColor("#F2F2F2")   //验证码输入框背景色
        private var etDivideSize = 6        //验证码输入框间距

        private var textColor = Color.BLACK //字体颜色
        private var textSize = 16.0F        //字体大小

        private var cursorColor = Color.parseColor("#FF2846")      //光标颜色
        public lateinit var myInterface : InputListener
        public fun setOnHandlerListener(myIntrf: InputListener){
            myInterface = myIntrf
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.captchaedit, this)
        if(!isSet) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.myEditTextAttrs, 0, 0)
            etCount = typedArray.getInteger(R.styleable.myEditTextAttrs_etCount, 6)
            etWidth = typedArray.getDimensionPixelSize(R.styleable.myEditTextAttrs_etWidth, 150)
            //etHeight = typedArray.getDimensionPixelSize(R.styleable.myEditTextAttrs_etWidth, 150)
            etBgColor =
                typedArray.getColor(R.styleable.myEditTextAttrs_etBgColor, Color.parseColor("#F2F2F2"))
            etDivideSize = typedArray.getDimensionPixelSize(R.styleable.myEditTextAttrs_etDivideSize, 6)
            textColor = typedArray.getColor(R.styleable.myEditTextAttrs_textColor, Color.BLACK)
            textSize =
                typedArray.getDimensionPixelSize(R.styleable.myEditTextAttrs_textSize, 16).toFloat()
            cursorColor = typedArray.getColor(
                R.styleable.myEditTextAttrs_cursorColor,
                Color.parseColor("#FF2846")
            )
            Log.i("myl","调用MyEditText初始化")
            typedArray.recycle()
            isSet = true
        }else{
            designUI()   //此时是翻转屏幕，直接show
        }

    }

    private fun designUI(){
        checkValidEt()
        initTextView()
        initUI()
        setListener()
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.i("myl","横屏")
        }
        else if(context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("myl","竖屏")
        }
        if(inputstr.isNotEmpty()) {
            setInitInput(inputstr.toString())
        }
        if(inputstr.length < etCount)
            initsoftware()
    }


    private fun checkValidEt(){
        //输入框的大小不能超过屏幕大小
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels//屏幕高度
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels//屏幕宽度
        Log.i("myl","高度$screenHeight, 宽度$screenWidth，修改前每个输入框大小$etWidth")
        if((etWidth+etDivideSize) * etCount >= screenWidth){
            etDivideSize = 6
            etWidth = (screenWidth/etCount)-etDivideSize
            //etHeight = etWidth
        }
        Log.i("myl","高度$screenHeight, 宽度$screenWidth，修改后每个输入框大小$etWidth")
    }

//    private fun getMyDisplay(context: Context) : Display?{
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//            return getMyDisplayR(context)
//        }else{
//            return getMyDisplayL(context)
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private fun getMyDisplayL(context: Context) : Display?{
//        val vm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        return vm.defaultDisplay
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.R)
//    private fun getMyDisplayR(context: Context) : Display? {
//        return context.display
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initTextView(){
        mRelativeLayouts.clear()
        for(i in 0 until etCount){
            val itemLayout = RelativeLayout(context)
            itemLayout.layoutParams = getEtLayoutParams(i)
            itemLayout.setBackgroundColor(etBgColor)
            //放入TextView
            val itemTextView = TextView(context)
            itemTextView.width = etWidth
            itemTextView.height = etWidth
            itemTextView.textSize = textSize
            itemTextView.setTextColor(textColor)
            itemTextView.gravity = Gravity.CENTER
            itemTextView.includeFontPadding = false
            modifyTextSize(itemTextView)
            itemLayout.addView(itemTextView)
            //放入光标
            val cursorView = View(context)
            initCursorView(cursorView)
            itemLayout.addView(cursorView)
            //放入数组中
            mRelativeLayouts.add(itemLayout)
            inputBox.add(itemTextView)
            cursorBox.add(cursorView)
        }
    }

    //修改文字大小
    @RequiresApi(Build.VERSION_CODES.O)
    private fun modifyTextSize(view: TextView){
        //Log.i("myl","$textSize,$etHeight")
        if(textSize > etWidth/3) {
            view.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        }
    }

    private fun initUI(){
        //默认输入框不可见
        inputEt.isCursorVisible = false
        inputEt.filters = arrayOf(InputFilter.LengthFilter(etCount))
        for(itemLayout in mRelativeLayouts) {
            totalView.addView(itemLayout)
        }
        for(i in 1 until etCount){
            cursorBox[i].isVisible = false
        }
        cursorBox[0].startAnimation(alphaAnimation)
    }



    //设置左右的外边距
    private fun getEtLayoutParams(i:Int) : LinearLayout.LayoutParams{
        val layoutParams = LinearLayout.LayoutParams(etWidth,etWidth)
        layoutParams.topMargin = 0
        layoutParams.bottomMargin = 0
        if(i==0){
            layoutParams.leftMargin = 0
            layoutParams.rightMargin = etDivideSize/2
        }else if(i==etCount-1){
            layoutParams.leftMargin = etDivideSize/2
            layoutParams.rightMargin = 0
        }else{
            layoutParams.leftMargin = etDivideSize/2
            layoutParams.rightMargin = etDivideSize/2
        }
        return layoutParams
    }

    //光标
    private fun initCursorView(view: View) {
        val layoutParams = LayoutParams(6,etWidth/2)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        view.setBackgroundColor(cursorColor)
        view.layoutParams = layoutParams
        //设置光标闪烁
        alphaAnimation = AlphaAnimation(0.1f,1.0f)
        alphaAnimation.duration = 1000
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.REVERSE
    }

    //设置监听
    private fun setListener(){
        inputEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                val str : String = p0?.toString() ?: ""
                if(str != ""){
                    showMyText(str)
                    inputEt.setText("")
                    if(inputstr.length < etCount)
                        inputstr.append(str)
                }
            }
        })
        inputEt.setOnKeyListener(object:OnKeyListener{
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if(p1== KeyEvent.KEYCODE_DEL && p2?.action == KeyEvent.ACTION_DOWN) {
                    deleteAllmyText()
                    inputstr.clear()
                    return true
                }
                return false
            }
        })
    }

    private fun showMyText(inputContent: String){
        for( i in 0 until etCount){
            if(inputBox[i].text.toString().trim() == "") {
                //Log.i("myl","$i")
                cursorBox[i].clearAnimation()
                cursorBox[i].isVisible = false
                inputBox[i].text = inputContent
                if(i == etCount-1) {
                    val inputManager : InputMethodManager = inputEt.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(inputEt.windowToken,0)
                    myInterface.finishInput()
                }else{
                    cursorBox[i+1].isVisible = true
                    cursorBox[i+1].startAnimation(alphaAnimation)

                }
                break
            }
        }
    }

    private fun deleteAllmyText(){
        for(i in 0 until etCount){
            inputBox[i].setText("")
            cursorBox[i].clearAnimation()
            cursorBox[i].isVisible = false
        }
        cursorBox[0].isVisible = true
        cursorBox[0].startAnimation(alphaAnimation)
    }

    private fun initsoftware(){
        inputEt.isFocusable = true
        inputEt.isFocusableInTouchMode = true
        inputEt.requestFocus()
        Timer().schedule(object: TimerTask(){
            override fun run() {
                val inputManager : InputMethodManager = inputEt.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(inputEt,0)
            }
        },2000)
    }

    //获取上次保留的数据(用于旋转屏幕)
    private fun setInitInput(str: String){
        Log.i("myl","翻转前的历史输入: $str")
        val strlen = str.length
        if(strlen > etCount) {
            Log.e("myl", "传入参数长度大小错误")
            return
        }
        for(i in 0 until strlen) {
            if (inputBox[i].text.toString().trim() == "") {
                cursorBox[i].clearAnimation()
                cursorBox[i].isVisible = false
                inputBox[i].text = str[i].toString()
            }
        }
        if(strlen==etCount) {
            val inputManager : InputMethodManager = inputEt.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(inputEt.windowToken,0)
        }else{
            cursorBox[strlen].isVisible = true
            cursorBox[strlen].startAnimation(alphaAnimation)
        }
    }

    //获得输入的接口
    fun getInput() : String{
        return inputstr.toString()
    }
    //显示接口
    fun show() {
        designUI()
    }

    //属性接口
    fun getetCount(): Int{
        return etCount
    }

    fun setetCount(i: Int){
        etCount = i
    }

    fun getetWidth(): Int{
        return etWidth
    }

    fun setetWidth(i: Int){
        etWidth = i

    }

    fun getetBgColor(): Int{
        return etBgColor
    }

    fun setetBgColor(i: Int){
        etBgColor = i
    }

    fun getetDivideSize(): Int{
        return etDivideSize
    }

    fun setetDivideSize(i: Int){
        etDivideSize = i
    }

    fun gettextColor(): Int{
        return textColor
    }

    fun settextColor(i: Int){
        textColor = i
    }

    fun gettextSize(): Float{
        return textSize
    }

    fun settextSize(i: Float){
        textSize = i
    }

    fun getcursorColor(): Int{
        return cursorColor
    }

    fun setcursorColor(i: Int){
        cursorColor = i
    }
 }