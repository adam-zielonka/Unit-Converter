package pro.adamzielonka.converter.tools;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.FileTools.getGson;

public class Open {
    public static ConcreteMeasure openConcreteMeasure(Context context, String fileName) throws FileNotFoundException {
        FileInputStream in = context.openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = getGson();
        return gson.fromJson(reader, ConcreteMeasure.class);
    }

    public static Measure openMeasure(Context context, String fileName) throws FileNotFoundException {
        FileInputStream in = context.openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = getGson();
        return gson.fromJson(reader, Measure.class);
    }

    public static Unit openUnit(String unitName, Measure measure) {
        for (Unit unit : measure.getUnits()) {
            if (unit.getUnitName().equals(unitName))
                return unit;
        }
        return null;
    }

    public static Prefix openPrefix(String prefixName, Unit unit) {
        for (Prefix prefix : unit.getPrefixes()) {
            if (prefix.getPrefixName().equals(prefixName))
                return prefix;
        }
        return null;
    }
}
