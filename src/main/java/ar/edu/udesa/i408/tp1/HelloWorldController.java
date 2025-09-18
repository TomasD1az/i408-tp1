package ar.edu.udesa.i408.tp1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @RequestMapping
    public String helloWorld() {
        return "Hello world";
    }

    @RequestMapping("/goodbye")
    public String goodbye() {
        return "Goodbye";
    }
}
