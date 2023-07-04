package com.example.tough;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.tough.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView text;  //输入格式错误提示
    private Button button;
    private StringBuilder str = new StringBuilder();  //参与运算的式子
    private int indexYN = 0;    //是否出错的标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editView);
        text = (TextView) findViewById(R.id.textView);
    }

    //数字及符号按钮
    public void clickButton(View view){
        button = (Button) view;
        editText.append(button.getText());
        str.append(button.getText());
    }

    //清空
    public void empty(View view){
        editText.setText(null);
        str.delete(0,str.length());
    }

    //delete最后一个字符
    public void delete(View view){
        String nowText = editText.getText().toString();
        if(nowText.length()!=0&&str.length()!=0){
            editText.setText(nowText.substring(0,nowText.length()-1));
            str.deleteCharAt(str.length()-1);
        }
    }
    //等于=
    public void equal(View view){
        indexYN = 0;  //重置为0，用于判断操作是否成功的标志
        System.out.println("str:\t"+str);
        System.out.println("lenth:\t"+str.length());
        estimate();
        if(indexYN==0){
            List<String> zhongZhui = zhuanZhongZhui(str.toString());
            System.out.println(zhongZhui);
            List<String> houZhui = zhuanHouZhui(zhongZhui);
            System.out.println(houZhui);
            editText.append("\n"+math(houZhui));
            str.delete(0,str.length());
            str.append(math(houZhui));
        }
    }

    //1/x
    public void reciprocal(View view){
        editText.append("1/");
        str.append("1/");
    }
    //开方
    public void squareRoot(View view){
        editText.append("√");
        str.append("l");
    }
    //%
    public void percentage(View view){
        editText.append("%");
        str.append("*0.01");
    }
    //π
    public void pi(View view){
        editText.append("π");
        str.append("p");
    }

    //sin
    public void sin(View view){
        editText.append("sin");
        str.append("s");
    }
    //cos
    public void cos(View view){
        editText.append("cos");
        str.append("c");
    }
    //tan
    public void tan(View view){
        editText.append("tan");
        str.append("t");
    }
    //取反
    public void Conversion(View view){
        editText.append("0-");
        str.append("0-");
    }


    private List<String> zhuanZhongZhui(String str) {//把输入的字符串转换成中缀表达式。存入list中
        int index = 0;  //初始化变量为0，用于迭代遍历字符串
        List<String> list = new ArrayList<>();//创建一个空的字符串列表来存储结果
        do{
            char ch = str.charAt(index);
            if("+-*/lsct()".indexOf(str.charAt(index)) >= 0){
                //是操作符，直接添加至list中
                index ++; //索引值+1
                list.add(ch+"");
            } else if (str.charAt(index) == 'p'){
                index ++;
                list.add(ch+"");
            } else if("0123456789".indexOf(str.charAt(index)) >= 0){
                //是数字,判断多位数的情况
                String str1 = "";
                while (index < str.length() && "0123456789.".indexOf(str.charAt(index)) >= 0){
                    str1 += str.charAt(index);
                    index ++;
                }
                list.add(str1);

            }
        }while (index < str.length());
        return list;
    }

//用于判断字符是否为数字、是否为操作符以及获取操作符的优先级
public List<String> zhuanHouZhui(List<String> list){//中缀表达式转换称后缀表达式
    Stack<String> fuZhan = new Stack<>();
    List<String> list2 = new ArrayList<>();
    if (!list.isEmpty()) {
        for (int i = 0; i < list.size(); i++) {
            if (isNumber(list.get(i))){
                list2.add(list.get(i));
            } else if (list.get(i).charAt(0) == '('){
                fuZhan.push(list.get(i));
            } else if (isOperator(list.get(i)) && list.get(i).charAt(0) != '('){
                if (fuZhan.isEmpty()){
                    fuZhan.push(list.get(i));
                } else {//符栈不为空
                    if (list.get(i).charAt(0) != ')'){
                        if (adv(fuZhan.peek()) <= adv(list.get(i))){
                            //入栈
                            fuZhan.push(list.get(i));
                        } else {//出栈
                            while (!fuZhan.isEmpty() && !"(".equals(fuZhan.peek())){
                                if(adv(list.get(i)) <= adv(fuZhan.peek())){
                                    list2.add(fuZhan.pop());
                                }
                            }
                            if (fuZhan.isEmpty() || fuZhan.peek().charAt(0) == '('){
                                fuZhan.push(list.get(i));
                            }
                        }
                    } else if (list.get(i).charAt(0) == ')'){
                        while (fuZhan.peek().charAt(0) != '('){
                            list2.add(fuZhan.pop());
                        }
                        fuZhan.pop();
                        }
                    }
                }
            }
        //  将符栈中剩余的运算符添加到后缀表达式列表中
            while (!fuZhan.isEmpty()){
                list2.add(fuZhan.pop());
            }
        } else {
            editText.setText("");
        }
        return list2;
    }
    public static boolean isOperator(String op){//判断是否为操作符
        if ("0123456789.p".indexOf(op.charAt(0)) == -1) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isNumber(String num){//判断是否为操作数
        if ("0123456789p".indexOf(num.charAt(0)) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int adv(String f){//判断操作符的优先级
        int result = 0;
        switch(f) {
            case "+":
                result = 1;
                break;
            case "-":
                result = 1;
                break;
            case "*":
                result = 2;
                break;
            case "/":
                result = 2;
                break;
            case "l":
                result = 3;
                break;
            case "s":
                result = 4;
                break;
            case "c":
                result = 4;
                break;
            case "t":
                result = 4;
                break;

        }
        return result;
    }

    public double math(List<String> list2) {//通过后缀表达式进行计算
        Stack<String> stack = new Stack<String>();//创建一个字符串栈来存储运算数
        for (int i = 0; i < list2.size(); i++) {
            if (isNumber(list2.get(i))) {
                if (list2.get(i).charAt(0) == 'p'){
                    stack.push(String.valueOf(Math.PI));
                } else {
                    stack.push(list2.get(i));
                }
            } else if (isOperator(list2.get(i))){
                double res = 0;
                if (list2.get(i).equals("+")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 + num2;
                } else if (list2.get(i).equals("-")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 - num2;
                } else if (list2.get(i).equals("*")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    res = num1 * num2;
                } else if (list2.get(i).equals("/")) {
                    double num2 = Double.parseDouble(stack.pop());
                    double num1 = Double.parseDouble(stack.pop());
                    if (num2 != 0){
                        res = num1 / num2;
                    } else {
                        editText.setText("除数不能为0");
                        indexYN = 1;
                    }
                } else if (list2.get(i).equals("l")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.sqrt(num1);
                } else if (list2.get(i).equals("s")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.sin(num1);
                } else if (list2.get(i).equals("c")) {
                    double num1 = Double.parseDouble(stack.pop());
                    res = Math.cos(num1);
                } else if (list2.get(i).equals("t")) {
                    double num1 = Double.parseDouble(stack.pop());
                    if (Math.cos(num1) != 0){
                        res = Math.tan(num1);
                    } else {
                        editText.setText("tan的x不能为+-(π/2 + kπ)");
                        indexYN = 1;
                    }
                }
                stack.push("" + res);
            }
        }
        if (indexYN == 0){
            if (!stack.isEmpty()){
                return Double.parseDouble(stack.pop());
            } else {
                return 0;
            }
        } else {
            return -999999;
        }
    }

    public void estimate (){//判断输入是否合理
        int i = 0;
        if (str.length() == 0){
            text.setText("输入为空！");
            indexYN = 1;
        }
        if (str.length() == 1){
            //当只有一位字符时，只能是“0123456789p”中的一个
            if ("0123456789ep".indexOf(str.charAt(0)) == -1){
                text.setText("当只有1位字符时，必须是0-9或p");
                indexYN = 1;
            }
        }
        if (str.length() > 1){
            for (i = 0; i < str.length() - 1; i++) {
                //第一个字符只能为"lsct(0123456789p"中的一个
                if ("lsct(0123456789p".indexOf(str.charAt(0)) == -1){
                    text.setText("第1个字符只能为\"lsct(0123456789p\"中的一个！");
                    indexYN = 1;
                }
                //“+-*/”后面只能是"0123456789lsct(p"中的一个
                if ("+-*/".indexOf(str.charAt(i)) >= 0 && "0123456789lsct(p".indexOf(str.charAt(i + 1)) == -1){
                    text.setText("+-*/后面只能是0123456789lsct(p中的一个");
                    indexYN = 1;
                }
                //"."后面只能是“0123456789”中的一个
                if (str.charAt(i) == '.' && "0123456789".indexOf(str.charAt(i + 1)) == -1){
                    text.setText(".后面只能是“0123456789中的一个");
                    indexYN = 1;
                }
                //"lsct"后面只能是“0123456789(p”中的一个
                if ("lsct".indexOf(str.charAt(i)) >= 0 && "0123456789(p".indexOf(str.charAt(i + 1)) == -1){
                    text.setText("lsct/后面只能跟常数或者是(");
                    indexYN = 1;
                }
                //"0"的判断操作
                if (str.charAt(0) == '0' && str.charAt(1) == '0'){
                    text.setText("当第一个字符和第二个字符都为0的时，则不对");
                    indexYN = 1;
                }
                if (i >= 1 && str.charAt(i) == '0'){
                    //&& str.charAt(0) == '0' && str.charAt(1) == '0'
                    int m = i;
                    int n = i;
                    int is = 0;
                    //1.当0的上一个字符不为"0123456789."时，后一位只能是“+-*/.)”中的一个
                    if ("0123456789.".indexOf(str.charAt(m - 1)) == -1 && "+-*/.)".indexOf(str.charAt(i + 1)) == -1){
                        text.setText("当0的上一个字符不为0123456789.时，后一位只能是+-*/.)");
                        indexYN = 1;
                    }
                    //2.如果0的上一位为“.”,则后一位只能是“0123456789+-*/.)”中的一个
                    if (str.charAt(m - 1) == '.' && "0123456789+-*/.)".indexOf(str.charAt(i + 1)) == -1){
                        text.setText("0的上一位为“.”,后一位只能是“0-9或+-*/.)”中的一个！");
                        indexYN = 1;
                    }
                    n -= 1;
                    while (n > 0){
                        if ("(+-*/lsct".indexOf(str.charAt(n)) >= 0){
                            break;
                        }
                        if (str.charAt(n) == '.'){
                            is++;
                        }
                        n--;
                    }

                    //3.如果0到上一个运算符之间没有“.”的话，整数位第一个只能是“123456789”，且后一位只能是“0123456789+-*/.)”中的一个。
                    if ((is == 0 && str.charAt(n) == '0') || "0123456789+-*/.)".indexOf(str.charAt(i + 1)) == -1){
                        text.setText("输入错误！");
                        indexYN = 1;
                    }
                    //4.如果0到上一个运算符之间有一个“.”的话,则后一位只能是“0123456789+-*/.)”中的一个
                    if (is == 1 && "0123456789+-*/.)".indexOf(str.charAt(i + 1)) == -1){
                        text.setText("输入错误！");
                        indexYN = 1;
                    }
                    if (is > 1){
                        text.setText("输入错误！");
                        indexYN = 1;
                    }
                }
                //7."123456789"后面只能是“0123456789+-*/.)”中的一个
                if ("123456789".indexOf(str.charAt(i)) >= 0 && "0123456789+-*/.)".indexOf(str.charAt(i + 1)) == -1){
                    text.setText("123456789后面只能是0123456789+-*/.)中的一个");
                    indexYN = 1;
                }
                //8."("后面只能是“0123456789locstg()p”中的一个
                if (str.charAt(i) == '(' && "0123456789locstg()p".indexOf(str.charAt(i + 1)) == -1){
                    text.setText("(\"后面只能是0123456789locstg()p中的一个");
                    indexYN = 1;
                }
                //9.")"后面只能是“+-*/)”中的一个
                if (str.charAt(i) == ')' && "+-*/)".indexOf(str.charAt(i + 1)) == -1){
                    text.setText("反括号后面只能是运算符或)");
                    indexYN = 1;
                }
                //10.最后一位字符只能是“0123456789)p”中的一个
                if ("0123456789!)ep".indexOf(str.charAt(str.length() - 1)) == -1){
                    text.setText("最后一位只能是0-9或)、p");
                    indexYN = 1;
                }
                //不能有多个“.”
                if (i > 2 && str.charAt(i) == '.'){
                    int n = i - 1;//向前遍历
                    int is = 0;
                    while (n > 0){
                        if ("(+-*/lsct".indexOf(str.charAt(n)) >= 0){
                            break;
                        }
                        if (str.charAt(n) == '.'){
                            is++;
                        }
                        n--;
                    }
                    if (is > 0){
                        text.setText("不能有多个.！");
                        indexYN = 1;
                    }
                }
                //13."p"后面只能是“+-*/)”中的一个
                if ("p".indexOf(str.charAt(i)) >= 0 && "+-*/)".indexOf(str.charAt(i + 1)) == -1){
                    text.setText("p后面只能是+ - */");
                    indexYN = 1;
                }
            }
        }
    }
}
