    package com.example.calculator

    import android.os.Build.VERSION_CODES
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.view.View
    import android.widget.Button
    import android.widget.TextView
    import androidx.core.text.isDigitsOnly
    import androidx.lifecycle.lifecycleScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
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
                currentInput.append("$buttonText")
                println("resulttxt : $currentInput")
                updateResultTextView()  }
            else{
                println("not appending operator")
            }
        }

        private fun onEqualsButtonClick(view: View){
            lifecycleScope.launch {
                try{
                    val result = calculator.evaluateExpression(currentInput.toString())
                    /*val result = evaluateExpression(currentInput.toString())*/
                    currentInput.clear()
                    currentInput.append(result)
                    println("The infix expression : $result")
                    updateResultTextView()
                }
                catch (e:Exception){
                    e.printStackTrace()//to print the exception details
                    currentInput.clear()
                    currentInput.append("Error in onEquals..()")
                    updateResultTextView()
                }
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
    //not calling this function. this is for the calculator without bodmass
    /*
        private fun evaluateExpression(expression: String):Double{
            //val elements = expression.split(Regex("([^0-9.])"))
            val numbers = expression.split(Regex("([^0-9.])")).filter { it.isNotBlank() && it!="." }.map { it.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $it")}
            val operators = expression.split(Regex("([^0-9.])")).filter { it.isNotBlank() }
            var result = */
    /*if (expression.startsWith("-")) -numbers[0] else*//*
     numbers[0]
            for (i in 1 until numbers.size){
                when(operators[i-1]){
                    "+" -> result += numbers[i]
                    "-" -> result -= numbers[i]
                    "×" -> result *= numbers[i]
                    "÷" -> result /= numbers[i]
                    "%" -> result %= numbers[i]
                }
            }
            return result
            currentInput.clear()
            currentInput.append(result.toString())
        }
    */

        private fun isOperator(s: String): Boolean{
            return s in listOf("+","-","÷","×","%")
        }

    }

    class Calculator{

        suspend fun evaluateExpression(expression:String): Double {
            return withContext(Dispatchers.Default){
                val postfixExpression = infixToPostfix(expression)
                return@withContext evaluatePostfixxpression(postfixExpression)
            }
        }

        private fun infixToPostfix(expression: String):String {
            val result = StringBuilder()
            var poppedOperator: Char? =null
            val operatorStack = Stack<Char>()
            for (char in expression) {
                if (char.isNumeric() || char == '.' ) {
                    result.append(char)
                    result.append(" ")
                    println("Appended $char in postfix expression")
                    println("The postfix expression : $result")
                } else if (char.isOperator()) {
                    while (operatorStack.isNotEmpty() && char.precedence() <= operatorStack.peek().precedence()){
                        poppedOperator = operatorStack.pop()
                        result.append(" ").append(poppedOperator).append(" ")
                        println("Appended $poppedOperator in postfix expression")
                        println("The postfix expression : $result")
                    }
                    operatorStack.push(char)
                }  else if(char == '('){
                    operatorStack.push(char)
                }  else if(char == ')'){
                    while(operatorStack.isNotEmpty() && operatorStack.peek() != ')'){
                        poppedOperator = operatorStack.pop()
                        result.append(" ").append(poppedOperator).append(" ")
                        println("Appending $poppedOperator in postfix expression")
                    }
                    operatorStack.pop()//this is to pop (
                }
            }
            while (operatorStack.isNotEmpty()){
                poppedOperator = operatorStack.pop()
                result.append(" ").append(poppedOperator).append(" ")
                println("Appending $poppedOperator in postfix expression")
            }
            println("Result = $result")//to check
            return result.toString().trim()///why is this trim is used - to delete leading and trailing spaces

        }

        private fun evaluatePostfixxpression(postfixExpression: String): Double {
            val stack = Stack<Double>()
            val decimal = "."
            val splitpostfixExpression = postfixExpression.split("\\s+".toRegex())
            val lenofpostfixExpression = splitpostfixExpression.size
            for (i in 0..<lenofpostfixExpression) {
                val char  = splitpostfixExpression[i]
                println("splitting the postfixexp at : $char")
                if (char.isNumeric()|| char == decimal) {
                    stack.push(char.toDouble())
                    println("$char has been pushed to the stack")

                }//not going inside this loop
                else if (char.isOperator()) {
                    println("$char has been pushed to the stack")

                    val operand2 = stack.pop()
                    val operand1 = stack.pop()
                    println("the popped operands are : $operand1,$operand2")
                    val result = when (char) {
                        "+" -> operand1 + operand2
                        "-" -> operand1 - operand2
                        "%" -> operand1 % operand2
                        "×" -> operand1 * operand2
                        "÷" -> operand1 / operand2
                        else -> throw IllegalArgumentException("Invalid operator : $char")
                    }
                    println("posfix operands result = $result")
                    stack.push(result)
                }
            }
            return if (stack.isEmpty()) {
                throw IllegalArgumentException("Invalid expression")
            } else {
                return stack.pop()
            }

        }


    /*
        private fun Char.isOperator(): Boolean {
            return this in setOf('+', '-', '×', '÷', '%')
        }
    */

        private fun Char.precedence(): Int {
            return when(this){
                '+','-' -> 1
                '×','÷','%' -> 2
                else -> 0
            }
        }
    }

    private fun Any.isOperator(): Boolean {
        return this in setOf('+', '-', '×', '÷', '%')
    }

    private fun Any.isNumeric(): Boolean{
        return this in setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    }



