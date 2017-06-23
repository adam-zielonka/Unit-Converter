package pro.adamzielonka.calculator.units;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static junit.framework.Assert.assertEquals;

public class UnitsTest {

    private final double delta = 0.01;

    @Test
    public void temperatureTemp() throws Exception {
        Gson gson = new Gson();

        try {
            Reader reader = new FileReader("./app/src/main/res/raw/converter_temperature.json");
            Units timeConverter = gson.fromJson(reader, Units.class);

            assertEquals(timeConverter.calculate(0.0, "°C", "°F"), 32.0, delta);
            assertEquals(timeConverter.calculate(0.0, "°C", "K"), 273.15, delta);
            assertEquals(timeConverter.calculate(0.0, "°C", "°R"), 491.67, delta);
            assertEquals(timeConverter.calculate(0.0, "°C", "°N"), 0, delta);
            assertEquals(timeConverter.calculate(0.0, "°C", "°Ré"), 0, delta);
            assertEquals(timeConverter.calculate(0.0, "°C", "°Rø"), 7.5, delta);
            assertEquals(timeConverter.calculate(0.0, "°C", "°De"), 150, delta);

            assertEquals(timeConverter.calculate(0.0, "°F", "°C"), -17.78, delta);
            assertEquals(timeConverter.calculate(0.0, "K", "°C"), -273.15, delta);
            assertEquals(timeConverter.calculate(0.0, "°R", "°C"), -273.15, delta);
            assertEquals(timeConverter.calculate(0.0, "°N", "°C"), 0, delta);
            assertEquals(timeConverter.calculate(0.0, "°Ré", "°C"), 0, delta);
            assertEquals(timeConverter.calculate(0.0, "°Rø", "°C"), -14.2857, delta);
            assertEquals(timeConverter.calculate(0.0, "°De", "°C"), 100, delta);

            String[] scales = {"K", "°C", "°F", "°R", "°De", "°N", "°Ré", "°Rø"};

            for (String scale1 : scales) {
                for (String scale2 : scales) {
                    System.out.println(scale2 + ": " + timeConverter.calculate(0.0, scale1, scale2));
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}