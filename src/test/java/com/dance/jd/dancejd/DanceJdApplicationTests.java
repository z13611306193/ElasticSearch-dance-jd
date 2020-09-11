package com.dance.jd.dancejd;

import com.dance.jd.dancejd.controller.ContentController;
import com.dance.jd.dancejd.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DanceJdApplicationTests {

    @Autowired
    ContentController contentController;

    @Test
    void testPutEs() throws Exception {

        boolean java = contentController.parse("java");
        System.out.println(java);

    }

}
