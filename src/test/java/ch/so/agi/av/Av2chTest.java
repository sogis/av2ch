package ch.so.agi.av;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.interlis2.validator.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.Ili2cException;
import ch.interlis.iox.IoxException;

public class Av2chTest {
    @BeforeAll 
    public static void setTimeout() {
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");
     }
    
    @AfterAll
    public static void resetTimeout() {
        System.setProperty("sun.net.client.defaultConnectTimeout", "-1");
        System.setProperty("sun.net.client.defaultReadTimeout", "-1");
     }

    @Test
    public void convert_SO_FileName_Ok(@TempDir Path tempDir) throws IllegalArgumentException, IoxException, Ili2cException {
        Path outputFilePath = tempDir.resolve("fubar_254900.itf");
        String outputParent = outputFilePath.toFile().getParent();
        String outputFileName = outputFilePath.toFile().getName();

        Av2ch av2ch = new Av2ch();
        av2ch.convert("src/test/data/254900.itf", outputParent, outputFileName, "de");
        
        long resultSize = outputFilePath.toFile().length();
        assertTrue(resultSize > 580000, "Size of result file is wrong.");
        
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, Validator.SETTING_DEFAULT_ILIDIRS);
        boolean valid = Validator.runValidation(outputFilePath.toFile().getAbsolutePath(), settings);
        assertTrue(valid, "Result file is not valid (ilivalidator).");
    }
    
    
    @Test
    public void convert_SO_Ok(@TempDir Path tempDir) throws IllegalArgumentException, IoxException, Ili2cException {    
        Path file = tempDir.resolve("ch_254900.itf");
        
        Av2ch av2ch = new Av2ch();
        av2ch.setModeldir("%XTF_DIR;http://models.interlis.ch/;%JAR_DIR");
        av2ch.convert("src/test/data/254900.itf", file.toFile().getParent(), null, "de");
        
        long resultSize = file.toFile().length();
        assertTrue(resultSize > 580000, "Size of result file is wrong.");
        
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, Validator.SETTING_DEFAULT_ILIDIRS);
        boolean valid = Validator.runValidation(file.toFile().getAbsolutePath(), settings);
        assertTrue(valid, "Result file is not valid (ilivalidator).");
    }
    
    @Test
    public void convert_SO_Fail(@TempDir Path tempDir) throws IllegalArgumentException, IoxException, Ili2cException {    
        Path file = tempDir.resolve("ch_fubar.itf");
        
        Av2ch av2ch = new Av2ch();
        
        try {
            av2ch.convert("src/test/data/fubar.itf", file.toFile().getParent(), null, "de");
        } catch (IoxException e) {
            assertTrue(e.getMessage().contains("could not parse file: fubar.itf"), "Exception message does not match.");
        }        
    }
}
