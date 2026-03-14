package com.example.expensetrackerapp

class Test {
}

fun main()
{
    var x = mutableListOf(0,1,1,0,1,1,1,0,1,1,1,0,0,0)
    var left = 0
    var right = 0
    for(i in 0..x.size-1)
    {
        if(x[i] == 0)
        {
            left++
        }
        else
        {
            right++
        }
    }

    if(left == right)
    {
        print("Current position is center")
    }
    else if(left > right)
    {
        print("Current position is left ${left - right}")
    }
    else
    {
        print("Current position is right ${right - left}")
    }
}