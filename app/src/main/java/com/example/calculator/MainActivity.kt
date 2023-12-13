package com.example.calculator

import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.lang.Exception
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private var currentInput = StringBuilder()
    private val calculator = Calculator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.resultTextView)

        setNumericButtonClickListener()
        setOperatorButtonClickListener()
        setEqualtoButtonClickListener()
        setClearButtonClickListener()
        setPlusMinusButtonClickListener()
        setDotBtnClickListener()
    }

    private fun setNumericButtonClickListener(){
        val numericalButtonIds = listOf(R.id.zeroBtn,R.id.oneBtn,R.id.twoBtn,R.id.threeBtn,R.id.fourBtn,R.id.fiveBtn,R.id.sixBtn,R.id.sevenBtn,R.id.eightBtn,R.id.nineBtn)
        for (buttonId in numericalButtonIds){
            findViewById<Button>(buttonId).setOnClickListener { onNumericButtonClick(it) }
        }
    }

    private fun setOperatorButtonClickListener(){
        val operatorButtonsIds = listOf(R.id.plusBtn,R.id.subtractBtn,R.id.multiplyBtn,R.id.divisionBtn,R.id.percentageBtn)
        for (buttonId in operatorButtonsIds){
            findViewById<Button>(buttonId).setOnClickListener { onOperatorButtonClick(it) }
        }
    }

    private fun setEqualtoButtonClickListener(){
        findViewById<Button>(R.id.equalsBtn).setOnClickListener { onEqualsButtonClick(it) }
    }

    private fun setClearButtonClickListener(){
        findViewById<Button>(R.id.clearBtn).setOnClickListener { onClearButtonClick(it) }
    }

    private fun setPlusMinusButtonClickListener(){
        findViewById<Button>(R.id.plusminusBtn).setOnClickListener { onPlusMinusButtonClick(it) }
    }

    private fun setDotBtnClickListener(){
        findViewById<Button>(R.id.dotBtn).setOnClickListener { onDotButton(it) }
    }
    private  fun onNumericButtonClick(view: View) {
        val buttonText = (view as Button).text
        currentInput.append(buttonText)
        updateResultTextView()
    }

    private fun onOperatorButtonClick(view: View){
        val buttonText = (view as Button).text
        /*val resultexp = resultTextView.text*/
        /*val elements = resultexp.split(Regex("([^0-9.])"))*/
        if (currentInput.isNotEmpty() && !isOperator(currentInput.last().toString())) {
            println("Appending operator : $buttonText")
            println("resulttxt : $currentInput")
            currentInput.append("$buttonText")
            updateResultTextView()  }
        else{
            println("not appending operator")
        }
    }

    private fun onEqualsButtonClick(view: View){
        try{
            val result = evaluateExpression(currentInput.toString())
            currentInput.clear()
            currentInput.append(result)
            updateResultTextView()
        }
        catch (e:Exception){
            currentInput.clear()
            currentInput.append("Error")
            updateResultTextView()
        }
    }

    private fun onClearButtonClick(view: View){
        currentInput.clear()
        updateResultTextView()
    }

    private fun onPlusMinusButtonClick(view: View){
        //need to implement the logic
        currentInput.insert(0,"-")
        updateResultTextView()
    }

    private fun onDotButton(view: View){
        val buttontext = (view as Button).text
        val resultexp = resultTextView.text
        val elements = resultexp.split(Regex("([^0-9.])"))

/*      println("lastchar - $lastchar")
        println("res - $resultexp")
        println("elements - $elements")
        println("last element - $lastelement")*/
        if(currentInput.isNotEmpty()){
            val lastelement = elements[elements.size - 1]//if resultexp is empty the app will shutdown
            val lastchar = currentInput.last().toString()

            if(!elements.isEmpty() && !lastelement.contains(".") && !isOperator(lastchar)){
                /* println("last char of curentInput : $lastchar")*/
                currentInput.append("$buttontext")
                updateResultTextView()
            }
        }

    }
    private fun updateResultTextView(){
        resultTextView.text = currentInput.toString()
    }

    private fun evaluateExpression(expression: String):String{
        //val elements = expression.split(Regex("([^0-9.])"))
        val numbers = expression.split(Regex("([^0-9.])")).filter { it.isNotBlank() && it!="." }.map { it.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $it")}
        val operators = expression.split(Regex("([0-9.])")).filter { it.isNotBlank() }
        var result = /*if (expression.startsWith("-")) -numbers[0] else*/ numbers[0]
        for (i in 1 until numbers.size){
            when(operators[i-1]){
                "+" -> result += numbers[i]
                "-" -> result -= numbers[i]
                "×" -> result *= numbers[i]
                "÷" -> result /= numbers[i]
                "%" -> result %= numbers[i]
            }
        }
        return result.toString()
        currentInput.clear()
        currentInput.append(result.toString())
    }

    private fun isOperator(s: String): Boolean{
        return s in listOf("+","-","÷","×","%")
    }
}

class Calculator{


    fun evaluateExpression(expression:String): Double {
        val postfixExpression = infixToPostfix(expression)
        return evaluatePostfixxpression(postfixExpression)
    }

    private fun infixToPostfix(expression: String):String {
        val result = StringBuilder()
        val operatorStack = Stack<Char>()
        for (char in expression) {
            if (char.isDigit() || char == '.' ) {
                result.append(char)
            } else if (char.isOperator()) {
                while (operatorStack.isNotEmpty() && char.precedence() <= operatorStack.peek().precedence()){
                    result.append(" ").append(operatorStack.pop()).append(" ")
                }
                operatorStack.push(char)
            }  else if(char == '('){
                operatorStack.push(char)
            }  else if(char == ')'){
                while(operatorStack.isNotEmpty() && operatorStack.peek() != ')'){
                    result.append(" ").append(operatorStack.pop()).append(" ")
                }
                operatorStack.pop()//this is to pop (
            }
        }
        while (operatorStack.isNotEmpty()){
            result.append(" ").append(operatorStack.pop()).append(" ")
        }
        return result.toString().trim()//why is this trim is used
    }

    private fun Char.isOperator(): Boolean {
        return this in setOf('+', '-', '×', '÷', '%')
    }

    private fun Char.precedence(): Int {
        return when(this){
            '+','-' -> 1
            '×','÷','%' -> 2
            else -> 0
        }
    }

}
