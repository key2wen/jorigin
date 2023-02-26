package com.key.jorigin.groovy;

/**
 * https://www.bootwiki.com/groovy/groovy-break-statement.html
 * Groovy 概述
 *  Groovy是一种基于Java平台的面向对象语言。 Groovy 1.0于2007年1月2日发布，其中Groovy 2.4是当前的主要版本。 Groovy通过Apache License v 2.0发布。
 *  Groovy的特点
 *  Groovy中有以下特点:
     * 同时支持静态和动态类型。
     * 支持运算符重载。
     * 本地语法列表和关联数组。
     * 对正则表达式的本地支持。
     * 各种标记语言，如XML和HTML原生支持。
     *  Groovy对于Java开发人员来说很简单，因为Java和Groovy的语法非常相似。
     * 您可以使用现有的Java库。
     *  Groovy扩展了java.lang.Object。
 *
 Groovy 可选: Groovy是一个“可选”类型的语言，当理解语言的基本原理时，这种区别是一个重要的语言。与Java相比，Java是一种“强”类型的语言，由此编译器知道每个变量的所有类型，并且可以在编译时理解和尊重合同。这意味着方法调用能够在编译时确定。
 当在Groovy中编写代码时，开发人员可以灵活地提供类型或不是类型。这可以提供一些简单的实现，并且当正确利用时，可以以强大和动态的方式为您的应用程序提供服务。
 在Groovy中，可选的键入是通过'def'关键字完成的。

 Groovy 范围: 范围是指定值序列的速记。范围由序列中的第一个和最后一个值表示，Range可以是包含或排除。包含范围包括从第一个到最后一个的所有值，而独占范围包括除最后一个之外的所有值。这里有一些范例文字的例子 -
     1..10 - 包含范围的示例
     1 .. <10 - 独占范围的示例
     'a'..'x' - 范围也可以由字符组成
     10..1 - 范围也可以按降序排列
     'x'..'a' - 范围也可以由字符组成并按降序排列。

 Groovy 列表列表是用于存储数据项集合的结构。在Groovy中，List保存了一系列对象引用。List中的对象引用占据序列中的位置，并通过整数索引来区分。列表文字表示为一系列用逗号分隔并用方括号括起来的对象。
 要处理列表中的数据，我们必须能够访问各个元素。 Groovy列表使用索引操作符[]索引。列表索引从零开始，这指的是第一个元素。
 以下是一些列表的示例 -
     [11，12，13，14] - 整数值列表
     ['Angular'，'Groovy'，'Java'] - 字符串列表
     [1，2，[3，4]，5] - 嵌套列表
     ['Groovy'，21，2.11] - 异构的对象引用列表
     [] - 一个空列表

 Groovy 映射映射（也称为关联数组，字典，表和散列）是对象引用的无序集合。Map集合中的元素由键值访问。 Map中使用的键可以是任何类。当我们插入到Map集合中时，需要两个值：键和值。
 以下是一些映射的例子 -
 ['TopicName'：'Lists'，'TopicName'：'Maps'] - 具有TopicName作为键的键值对的集合及其相应的值。
 [：] - 空映射。

 Groovy 正则表达式正则表达式是用于在文本中查找子字符串的模式。 Groovy使用〜“regex”表达式本地支持正则表达式。引号中包含的文本表示用于比较的表达式。
 例如，我们可以创建一个正则表达式对象，如下所示 -
 def regex = ~'Groovy'


 Groovy 面向对象在Groovy中，如在任何其他面向对象语言中一样，存在类和对象的概念以表示编程语言的对象定向性质。Groovy类是数据的集合和对该数据进行操作的方法。在一起，类的数据和方法用于表示问题域中的一些现实世界对象。
 Groovy中的类声明了该类定义的对象的状态（数据）和行为。因此，Groovy类描述了该类的实例字段和方法。
 以下是Groovy中的一个类的示例。类的名称是Student，它有两个字段 - StudentID和StudentName。在main函数中，我们创建一个这个类的对象，并将值分配给对象的StudentID和StudentName。
 class Student {
 int StudentID;
 String StudentName;
 static void main(String[] args) {
 Student st = new Student();
 st.StudentID = 1;
 st.StudentName = "Joe"
 }
 }

 Groovy 泛型在定义类，接口和方法时，泛型使能类型（类和接口）作为参数。与在方法声明中使用的更熟悉的形式参数非常类似，类型参数提供了一种方法，可以为不同的输入重复使用相同的代码。区别在于形式参数的输入是值，而类型参数的输入是类型。
 集合的通用
 可以对集合类（如List类）进行一般化，以便只有该类型的集合在应用程序中被接受。下面显示了一般化ArrayList的示例。以下语句的作用是它只接受类型为string的列表项 -
 List list = new ArrayList();
 https://www.bootwiki.com/groovy/groovy-generics.html

 Groovy 闭包closure
 https://www.bootwiki.com/groovy/groovy-closures.html
 def lst = [1,2,3,4];
 lst.each {println it}
 lst.each{num -> if(num % 2 == 0) println num}


 Groovy DSLs
 Groovy DSLS Groovy允许在顶层语句的方法调用的参数周围省略括号。这被称为“命令链”功能。这个扩展的工作原理是允许一个人链接这种无括号的方法调用，在参数周围不需要括号，也不需要链接调用之间的点。
 如果一个调用被执行为bcd，这将实际上等价于a（b）.c（d）。
 DSL或域特定语言旨在简化以Groovy编写的代码，使得它对于普通用户变得容易理解。
 https://www.jianshu.com/p/cae3798513b2
 https://www.bootwiki.com/groovy/groovy-dsls.html
 https://www.wenjiangs.com/doc/r81toubr
 什么是 DSL？
 Martin Fowler 普及了特定于领域语言的理念（请参阅 参考资料）。他把 DSL 定义为
 “侧重特定领域的表达有限的计算机编程语言”。“有限的表达” 并不是指语言的用途有限，只是表示这种语言提供了足够用于适当表达 “特定领域” 的词汇表。DSL 是一种很小的专用语言，这与 Java 语言等大型通用语言形成对比。
 SQL 就是一种优秀的 DSL。您无法使用 SQL 编写操作系统，但它是处理关系数据库这一有限领域的理想选择。
 在同样意义上，Groovy 是 Java 平台的 DSL，因为它是有限领域的 Java 开发的理想选择。

 *
 */
public class Groovy {
}

/**
 class Example_ForIn_Break {
 static void main(String[] args) {
 int[] array = [0,1,2,3];

 for(int i in array) {
 println(i);
 if(i == 2)
 break;
 }
 }
 }


 static void switch(String[] args) {
 //initializing a local variable
 int a = 2

 //Evaluating the expression value
 switch(a) {
 //There is case statement defined for 4 cases
 // Each case statement section has a break condition to exit the loop

 case 1:
 println("The value of a is One");
 break;
 case 2:
 println("The value of a is Two");
 break;
 case 3:
 println("The value of a is Three");
 break;
 case 4:
 println("The value of a is Four");
 break;
 default:
 println("The value is unknown");
 break;
 }
 }
 }

 class Example_method {
 static def DisplayName() {
 println("This is how methods work in groovy");
 println("This is an example of a simple method");
 }

 static void main(String[] args) {
 DisplayName();
 }
 }

 //Groovy中还有一个规定来指定方法中的参数的默认值。 如果没有值传递给参数的方法，则使用缺省值。 如果使用非默认和默认参数，则必须注意，默认参数应在参数列表的末尾定义
 class Example_defaultParameter {
 static void sum(int a,int b = 5) {
 int c = a+b;
 println(c);
 }

 static void main(String[] args) {
 sum(6);
 sum(6,6);
 }
 }






 */



