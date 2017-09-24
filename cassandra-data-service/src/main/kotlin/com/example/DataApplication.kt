package com.example

import com.example.util.run
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DataApplication


fun main(args: Array<String>) {
    run(DataApplication::class, *args)
}
