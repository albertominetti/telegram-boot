package it.minetti.config;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCallerOptions;
import com.github.rcaller.rstuff.RCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class SampleTest {
    @PostConstruct
    public void mean() {

        log.info("Start R");
        String fileContent = "customMean <- function(vector) {\n" +
                "    mean(vector)\n" +
                "}";
        RCode code = RCode.create();
        code.addRCode(fileContent);
        code.addIntArray("input", new int[]{122, 59, 632, 96});
        code.addRCode("result <- customMean(input)");
        RCaller caller = RCaller.create(code, RCallerOptions.create());
        caller.runAndReturnResult("result");

        log.info("Result is: {}", caller.getParser().getAsDoubleArray("result")[0]);

        return;
    }
}
