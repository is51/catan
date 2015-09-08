;
(function () {
    function lib() {
        return (function () {
            var result = "\n";
            for (var key in lib) {
                result += "\t" + key + "();\n";
            }
            return result;
        })();
    }

    function numberOfMethods() {
        var count = 0;
        for (var key in lib) {
            count++;
        }

        return count;
    }

    window.lib = lib;
    lib.numberOfMethods = numberOfMethods;

    lib.makeBufferExample = makeBufferExample;
    lib.sumExample = sumExample;
    lib.callNewFunctionThatIsReturnedFromFunctionWithDoubleBrackets = callNewFunctionThatIsReturnedFromFunctionWithDoubleBrackets;
    lib.stateOfOuterVariableAndInnerFunctionWithSameNames = stateOfOuterVariableAndInnerFunctionWithSameNames;
    lib.stateOfOuterVariableAndInnerVariableWithSameNames = stateOfOuterVariableAndInnerVariableWithSameNames;
    lib.selfExecutionFunction = selfExecutionFunction;
    lib.stateOfOuterVariableAndUsageItInFunction = stateOfOuterVariableAndUsageItInFunction;
    lib.valueOfVariableThatIsNotAssignedYet = valueOfVariableThatIsNotAssignedYet;
    lib.timingUsageAndMethodPassingToFunction = timingUsageAndMethodPassingToFunction;
    lib.sortByFieldNameExample = sortByFieldNameExample;
    lib.filterByFunction = filterByFunction;
    lib.armyOfShooters = armyOfShooters;
    lib.workWithObjects = workWithObjects;
    lib.calculatorExample = calculatorExample;
    lib.leaderChaining = leaderChaining;
    lib.objectMapping = objectMapping;
    lib.accumulatorExample = accumulatorExample

    //TODO:
    lib.newCalculatorExample = newCalculatorExample;
    lib.defineObjects = defineObjects;
    lib.articleCounter = articleCounter;
    lib.thisFunctions = thisFunctions;
    lib.sumFunction = sumFunction;
    lib.applyFunction = applyFunction;
    lib.bindExamples = bindExamples;


    function bindExamples() {
        function User(name) {
            this.firstName = name;
            this.sayHi =  function(who, number) {
                console.log( this.firstName + ": Привет, " + (who === undefined ? "empty" : who.firstName) + " number: " + number);
            };
        }

        var user = new User("Вася");
        var whoUser = new User("Петя");
        var number = 0;
        setTimeout(user.sayHi, 1000); // undefined (не Вася!) - context lost






        var user1 = new User("Вася");
        var whoUser1 = new User("Петя");
        var number1 = 1;

        setTimeout(function() {
            user1.sayHi(whoUser1, number1); // Андрей: Привет, Таня - context saved but changed (primitive changed)
        }, 2000);

        //change contexts
        user1.firstName = "Андрей";
        whoUser1.firstName = "Таня";
        number1 = 100;








        var user2 = new User("Вася");
        var whoUser2 = new User("Петя");
        var number2 = 2;

        function bind(func, context){
            return function(){
                return func.apply(context, arguments);
            }
        }

        var sayHiBinded = bind(user2.sayHi, user2);

        setTimeout(function() {
            sayHiBinded(whoUser2, number2); // Андрей: Привет, Таня - context saved but changed (primitive changed)
        }, 2000);

        //change contexts
        user2.firstName = "Андрей";
        whoUser2.firstName = "Таня";
        number2 = 200;




        var user3 = new User("Вася");
        var whoUser3 = new User("Петя");
        var number3 = 3;

        setTimeout(user3.sayHi.bind(user3, whoUser3, number3), 2000);  // Андрей: Привет, Таня - context saved but changed (primitive saved)

        //change contexts
        user3.firstName = "Андрей";
        whoUser3.firstName = "Таня";
        number3 = 300;

        var a = [1,2,3,5,2];
        a.forEach(console.log.bind(console));  //log() contains refer to 'this', so we should bind it to 'console' context

    }

    function applyFunction() {
        // Напишите функцию applyAll(func, arg1, arg2...), которая получает функцию func и произвольное количество аргументов.

        //       Она должна вызвать func(arg1, arg2...), то есть передать в func все аргументы, начиная со второго, и возвратить результат.

        //     Например:

        // Применить Math.max к аргументам 2, -2, 3
        alert(applyAll(Math.max, 2, -2, 3)); // 3

        // Применить Math.min к аргументам 2, -2, 3
        alert(applyAll(Math.min, 2, -2, 3)); // -2
        // Область применения applyAll, конечно, шире, можно вызывать её и со своими функциями:


        function sum() { // суммирует аргументы: sum(1,2,3) = 6
            return [].reduce.call(arguments, function (a, b) {
                return a + b;
            });
        }

        function mul() { // перемножает аргументы: mul(2,3,4) = 24
            return [].reduce.call(arguments, function (a, b) {
                return a * b;
            });
        }

        alert(applyAll(sum, 1, 2, 3)); // -> sum(1, 2, 3) = 6
        alert(applyAll(mul, 2, 3, 4)); // -> mul(2, 3, 4) = 24
    }

    function sumFunction() {
        function sum(arr) {
            return arr.reduce(function (a, b) {
                return a + b;
            });
        }

        alert(sum([1, 2, 3])); // 6 (=1+2+3)

        function sumArgs() {
            //Создайте аналогичную функцию sumArgs(), которая будет суммировать все свои аргументы:
            //Для решения примените метод reduce к arguments, используя call, apply или одалживание метода.
            //P.S. Функция sum вам не понадобится, она приведена в качестве примера использования reduce для похожей задачи.

            /* ваш код */
        }

        alert(sumArgs(1, 2, 3)); // 6, аргументы переданы через запятую, без массива
    }

    function thisFunctions() {
        var user = {
            firstName: "Василий",
            surname: "Петров",
            patronym: "Иванович"
        };

        function showFullName(firstPart, lastPart) {
            console.log(this[firstPart] + " " + this[lastPart]);
        }

        // f.call(контекст, аргумент1, аргумент2, ...)
        showFullName.call(user, 'firstName', 'surname'); // "Василий Петров"
        showFullName.call(user, 'firstName', 'patronym'); // "Василий Иванович"

    }

    function articleCounter() {
        function Article() {
            this.created = new Date();
            // ... ваш код ...
        }

        new Article();
        new Article();

        Article.showStats(); // Всего: 2, Последняя: (дата)

        new Article();

        Article.showStats(); // Всего: 3, Последняя: (дата)
    }

    function defineObjects() {
        function User(fullName) {
            this.fullName = fullName;
        }

        var vasya = new User("Василий Попкин");

        // чтение firstName/lastName
        alert(vasya.firstName); // Василий
        alert(vasya.lastName); // Попкин

        // запись в lastName
        vasya.lastName = 'Сидоров';

        alert(vasya.fullName); // Василий Сидоров

        //fullName should be field (not method)`
    }


    function newCalculatorExample() {
        var calc = new Calculator;

        alert(calc.calculate("3 + 7")); // 10

        var powerCalc = new Calculator;
        powerCalc.addMethod("*", function (a, b) {
            return a * b;
        });
        powerCalc.addMethod("/", function (a, b) {
            return a / b;
        });
        powerCalc.addMethod("**", function (a, b) {
            return Math.pow(a, b);
        });

        var result = powerCalc.calculate("2 ** 3");
        alert(result); // 8
    }

    function accumulatorExample() {
        function Accumulator(initialValue) {
            this.value = initialValue;
            this.read = function () {
                this.value += +prompt("Enter value to accumulate:");
            }
        }


        var accumulator = new Accumulator(1); // начальное значение 1
        accumulator.read(); // прибавит ввод prompt к текущему значению
        accumulator.read(); // прибавит ввод prompt к текущему значению
        console.log(accumulator.value); // выведет текущее значение
    }

    function objectMapping() {
        /////// BOOLEAN mapping
        console.log("////// BOOLEAN mapping");
        //empty object and array
        if ({} && []) {
            console.log("Все объекты - true!");
        }

        console.log([] == []);    // false - checks objects
        console.log([] == ![]);   // true - converts right to boolean, than call toString of array,
        // it returns empty list: '', then converts boolean and string both to numeric: false is 0 and '' is 0;

        ////// STRING mapping
        console.log("////// STRING mapping");

        var user = {
            firstName: 'Василий',
            toString: function () {
                return "User " + this.firstName;
            }
        };
        console.log(user.toString()); // Object {firstName: "Василий"}

        console.log(['x'] == 'x'); // converts array to string with elements, split by comma: ['a','b' ].toString() returns 'x,y'

        ////// NUMERIC mapping
        console.log("////// NUMERIC mapping");

        var foo = {
            toString: function () {
                return 'foo';
            },
            valueOf: function () {
                return 2;
            }
        };

        console.log(foo);         // Object {}
        console.log(foo + 1);     // 3
        console.log(foo + "3");   // 23


        console.log("CALCULATOR:");

        console.log(sum(1)(2) == 3); // 1 + 2
        console.log(sum(1)(2)(3) == 6);  // 1 + 2 + 3
        console.log(sum(5)(-1)(2) == 6);
        console.log(sum(6)(-1)(-2)(-3) == 0);
        console.log(sum(0)(1)(2)(3)(4)(5) == 15);

        function sum(arg) {
            var result = arg;

            return function s(argS) {
                result += argS;
                s.toString = function () {
                    console.log(result);
                    return result;
                };

                return s;
            };
        }
    }

    function leaderChaining() {
        var ladder = {
            step: 0,
            up: function () { // вверх по лестнице
                this.step++;

                return this;
            },
            down: function () { // вниз по лестнице
                this.step--;

                return this;
            },
            showStep: function () { // вывести текущую ступеньку
                console.log(this.step);
                return this;
            }
        };

        ladder
                .up()
                .up()
                .down()
                .up()
                .up()
                .down()
                .showStep(); // 2
    }

    function calculatorExample() {
        function Calculator() {
            this.readParams = function () {
                this.first = prompt("Enter first number:");
                this.second = prompt("Enter second number:");
            };

            this.sum = function () {
                return +this.first + +this.second;
            };

            this.mul = function () {
                return +this.first * +this.second;
            };
        }

        var calculator = new Calculator();

        calculator.readParams();
        console.log(calculator.sum());
        console.log(calculator.mul());
    }

    function workWithObjects() {
        function Person() {
            this.name = "NOT ASSIGNED";

            this.setName = function setNameFunction(name) {
                this.name = name;
            };

            this.setAge = function setAgeFunction(age) {
                this.age = age;
            };

        }

        var vanya = new Person();
        vanya.setName("Vanya");
        vanya.setAge(2);

        var andrey = new Person();
        andrey.setName("Andrey");
        andrey.setAge(5);

        var tanya = new Person();
        tanya.setName("Tanya");
        tanya.setAge(4);

        console.log(vanya);
        console.log(andrey);
        console.log(tanya);

        function func() {
            "use strict";
            console.log(this); // выведет undefined (кроме IE9-)
        }

        func();

        var arr = ["a", "b"];

        arr.push(function () {
            console.log("bla bla");

        });

        arr.push(function () {
            console.log("object 'this': " + this);
        });

        console.log("calling arr[2] : ");
        arr[2]();
        console.log("calling arr[3] : ");
        arr[3]();

        console.log("#### accessing object methods: ");
        "use strict";
        var obj, method;
        obj = {
            go: function () {
                console.log(this)
            }
        };

        obj.go();               // (1) object
        (obj.go)();             // (2) object
        (method = obj.go)();    // (3) undefined  ('=' - when calling something instead of '()', context is lost)
        (obj.go || obj.stop)(); // (4) undefined  ('||' - when calling something instead of '()', context is lost)


        var username = {
            name: "Василий",

            exportThis: function () {
                return this;    //returns object itself
            }

        };

        var thisExported = username.exportThis();
        console.log(thisExported.name);
    }

    function armyOfShooters() {
        function makeArmy() {

            var shooters = [];

            for (var i = 0; i < 10; i++) {

                var shooter = (function (x) { // pass x so that it is never change in LexicalEnvironment of this "in-place" function
                    return function () { // функция-стрелок
                        console.log("shot " + x); // выводит свой номер
                    }
                }(i));

                shooter.index = i;

                shooters.push(shooter);
            }

            return shooters;
        }

        var army = makeArmy();

        army[0]();
        army[5]();
    }

    function filterByFunction() {
        function filter(array, func) {
            return array.reduce(function (array, value) {
                if (func(value)) {
                    array.push(value);
                }

                return array;
            }, []);
        }

        function inBetween(low, high) {
            return function (value) {
                return value >= low && value <= high;
            }
        }

        function inArray() {
            var validArray = arguments[0];
            return function (value) {
                for (var i = 0; i < validArray.length; i++) {
                    if (value === validArray[i]) {
                        return true;
                    }
                }

                return false;
            }

        }

        /* .. ваш код для filter, inBetween, inArray */
        var arr = [1, 2, 3, 4, 5, 6, 7];

        console.log(filter(arr, function (a) {
            return a % 2 == 0
        })); // 2,4,6

        console.log(filter(arr, inBetween(3, 6))); // 3,4,5,6
        console.log(filter(arr, inArray([1, 2, 10]))); // 1,2
    }

    function sortByFieldNameExample() {
        function byField(fieldName) {
            return function (a, b) {
                return a[fieldName] > b [fieldName];
            }
        }

        var users = [{
            name: "Вася",
            surname: 'Иванов',
            age: 20
        }, {
            name: "Петя",
            surname: 'Чапаев',
            age: 25
        }, {
            name: "Маша",
            surname: 'Медведева',
            age: 18
        }];

        users.sort(byField('name'));
        users.forEach(function (user) {
            console.log(user.name);
        }); // Вася, Маша, Петя

        users.sort(byField('age'));
        users.forEach(function (user) {
            console.log(user.name);
        }); // Маша, Вася, Петя
    }

    function makeBufferExample() {
        function makeBuffer() {
            //bufferedString = ""; //try without 'var' - result the same
            var bufferedString = "";


            function buffer() {
                if (arguments.length > 0) {
                    bufferedString += arguments[0];
                } else {
                    return bufferedString;
                }
            }

            buffer.clear = function () {
                bufferedString = "";
            };

            return buffer;
        }

        var bufferString = makeBuffer();
        bufferString('Замыкания');
        bufferString(' Использовать');
        bufferString(' Нужно!');

        console.log(bufferString()); // Замыкания Использовать Нужно!

        var bufferNumeric = makeBuffer();
        bufferNumeric(0);
        bufferNumeric(1);
        bufferNumeric(0);

        console.log(bufferNumeric()); // 010 - if 'var' before bufferedString, or even without

        bufferNumeric.clear();
        console.log(bufferNumeric()); // ""

        bufferNumeric(5);
        console.log(bufferNumeric()); // 5
    }

    function sumExample() {
        function sum(firstParam) {
            var sumResult = firstParam;
            return function (secondParam) {
                return sumResult + secondParam;
            }
        }

        var sumResult = sum(1)(-2);
        console.log(sumResult);

    }

    function callNewFunctionThatIsReturnedFromFunctionWithDoubleBrackets() {
        var a = 1; //TODO: PUT IT TO GLOBAL SCOPE

        function getFunc() {
            var a = 2;

            var func = new Function('', 'console.log(a)');

            return func;
        }

        getFunc()(); // 1, from window, qoz [[Scope]] refers to global object of variables
    }

    function selfExecutionFunction() {
        var a = 5;

        (function () {
            console.log(a)
        })();
    }


    /*
     variable is being initialized in outer method, and function with same name is being initialized
     at the beginning of execution, so it is populated to LexicalEnvironment of function b, but has value 'function',
     and when we try to populate 'variable' with value 10, it searches it in LE, find variable 'function' and set value 10,
     outer variables is not changed
     */
    function stateOfOuterVariableAndInnerFunctionWithSameNames() {
        var variable = 1;

        function b() {
            variable = 10;


            function variable() {
            }

            console.log(variable);
            return;

        }

        b();

        console.log(variable);
    }

    /*
     variable  'value' is being initialized in outer method, and variable 'value' with same name is being initialized
     at the beginning of execution of function 'f', so it is populated to LexicalEnvironment of function f
     and has value 'undefined', and when we try to populate variable 'value' with 'true', it searches it in LE,
     find variable 'value' and set value 'true', outer variables is not changed
     */
    function stateOfOuterVariableAndInnerVariableWithSameNames() {

        var value = 0;

        function f() {
            if (1) {
                value = true;
            } else {
                var value = false;
            }

            console.log(value);
        }

        f();
        console.log(value);
    }


    function stateOfOuterVariableAndUsageItInFunction() {

        var value1 = 0;

        function f() {
            if (1) {
                value1 = true;
            } else {
                value1 = false;
            }

            console.log(value1);
        }

        f();
        console.log(value1);
    }


    function valueOfVariableThatIsNotAssignedYet() {

        function test() {
            console.log(window);
            var window = 5;
            console.log(window);
        }

        test();
    }


    function timingUsageAndMethodPassingToFunction() {
        console.time("page");
        function walkReduce(arr) {
            return arr.reduce(function (result, current) {
                if (current < 0) {
                    result["less 0"].push(current);
                } else if (current === 0) {
                    result["equal 0"].push(current);
                } else {
                    result["grater 0"].push(current);
                }

                return result;
            }, {
                "less 0": [],
                "equal 0": [],
                "grater 0": []
            });
        }

        function walkLength(arr) {
            var result = {
                "less 0": [],
                "equal 0": [],
                "grater 0": []
            };

            for (var i = 0; i < arr.length; i++) {
                if (arr[i] < 0) {
                    result["less 0"].push(arr[i]);
                } else if (arr[i] === 0) {
                    result["equal 0"].push(arr[i]);
                } else {
                    result["grater 0"].push(arr[i]);
                }
            }

            return result;
        }


        var values = new Array(10000);
        for (var i = 0; i < values.length; i++) {
            values[i] = Math.round(Math.random() * 10) - 5;
        }

        var testCase = function (testArray, testFunction) {
            var startTime = new Date();
            for (var k = 0; k < 10; k++) {
                testFunction(testArray);
            }
            var endTime = new Date();

            return endTime - startTime;
        };


        var walkReduceTime = 0;
        var walkLengthTime = 0;

        var walkReduceResult = walkReduce(values);
        var walkLengthResult = walkLength(values);
        var methodsCheck = walkReduceResult["less 0"].length === walkLengthResult["less 0"].length
        && walkReduceResult["equal 0"].length === walkLengthResult["equal 0"].length
        && walkReduceResult["grater 0"].length === walkLengthResult["grater 0"].length
                ? "methods work consistently"
                : "methods work in different ways!!!";

        console.log("walkReduceResult less 0: " + walkReduceResult["less 0"].length
        + ", walkLengthResult less 0: " + walkLengthResult["less 0"].length);
        console.log("walkReduceResult equal 0: " + walkReduceResult["equal 0"].length
        + ", walkLengthResult equal 0: " + walkLengthResult["equal 0"].length);
        console.log("walkReduceResult grater 0: " + walkReduceResult["grater 0"].length
        + ", walkLengthResult grater 0: " + walkLengthResult["grater 0"].length);
        console.log(methodsCheck);

        for (var j = 0; j < 100; j++) {
            walkReduceTime += testCase(values, walkReduce);
            walkLengthTime += testCase(values, walkLength)
        }

        console.log("walkReduce result time: " + walkReduceTime);
        console.log("walkLength result time: " + walkLengthTime);
        console.log("\nexecution time: " + Math.round(performance.now()));
        console.timeEnd("page");

    }
})();