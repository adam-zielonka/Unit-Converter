package pro.adamzielonka.converter.tools;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;

public class Save {
    public static void saveMeasure(Context context, ConcreteMeasure concreteMeasure, Measure userMeasure) throws IOException {
        Gson gson = new Gson();
        String concreteFileName = concreteMeasure.getConcreteFileName();
        String userFileName = concreteMeasure.getUserFileName();
        concreteMeasure = userMeasure.getConcreteMeasure(concreteFileName, userFileName);
        saveToInternal(context, concreteFileName, gson.toJson(concreteMeasure));
        saveToInternal(context, userFileName, gson.toJson(userMeasure));
    }
}
