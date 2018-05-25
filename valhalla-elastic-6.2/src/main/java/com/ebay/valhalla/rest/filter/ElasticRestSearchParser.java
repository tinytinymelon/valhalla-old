package com.ebay.valhalla.rest.filter;

import com.ebay.valhalla.api.filter.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

public class ElasticRestSearchParser {
    public static JsonObject parse(List<ElasticFilter> filters) {
        if (filters.size() == 1) {
            return getFilterJson(filters.get(0));
        }

        JsonObject boolObj = new JsonObject();
        JsonObject mustObj = new JsonObject();
        JsonArray mustArray = new JsonArray();

        boolObj.add("bool", mustObj);
        mustObj.add("must", mustArray);

        for (ElasticFilter filter : filters) {
            mustArray.add(getFilterJson(filter));
        }

        return boolObj;
    }

    public static JsonObject parse(ElasticFilter[] filters) {
        if (filters.length == 1) {
            return getFilterJson(filters[0]);
        }

        JsonObject boolObj = new JsonObject();
        JsonObject mustObj = new JsonObject();
        JsonArray mustArray = new JsonArray();

        boolObj.add("bool", mustObj);
        mustObj.add("must", mustArray);

        for (ElasticFilter filter : filters) {
            mustArray.add(getFilterJson(filter));
        }

        return boolObj;
    }

    private static JsonObject getFilterJson(ElasticFilter filter) {
        JsonObject queryObj = new JsonObject();
        JsonObject object = new JsonObject();

        switch (filter.type()) {
            case TERM:

                ElasticTermFilter f = (ElasticTermFilter) filter;
                if (f.isString()) {
                    object.add(f.getField(), new JsonPrimitive(f.getStringValue()));
                } else if (f.isNumber()) {
                    object.add(f.getField(), new JsonPrimitive(f.getNumberValue()));
                } else if (f.isBoolean()) {
                    object.add(f.getField(), new JsonPrimitive(f.getBooleanValue()));
                }

                queryObj.add("term", object);
                break;

            case MATCH:
                ElasticMatchFilter mf = (ElasticMatchFilter) filter;
                if (mf.isString()) {
                    object.add(mf.getField(), new JsonPrimitive(mf.getStringValue()));
                } else if (mf.isNumber()) {
                    object.add(mf.getField(), new JsonPrimitive(mf.getNumberValue()));
                } else if (mf.isBoolean()) {
                    object.add(mf.getField(), new JsonPrimitive(mf.getBooleanValue()));
                }

                queryObj.add("match", object);
                break;

            case RANGE:
                queryObj.add("range", buildRangeObj((ElasticLongRangeFilter) filter));
                break;

            case EXISTS:
                queryObj.add("exists", buildExistsObj((ElasticExistsFilter) filter));
        }

        return queryObj;
    }

    private static JsonObject buildRangeObj(ElasticLongRangeFilter longFilter) {
        JsonObject valueObj = new JsonObject();

        switch (longFilter.op) {
            case BOTH:
                valueObj.addProperty("gte", longFilter.start);
                valueObj.addProperty("lte", longFilter.end);
                break;

            case LARGER:
                valueObj.addProperty("gte", longFilter.start);
                break;

            case SMALLER:
                valueObj.addProperty("lte", longFilter.start);
                break;
        }

        JsonObject rangeObj = new JsonObject();
        rangeObj.add(longFilter.field, valueObj);

        return rangeObj;
    }

    private static JsonObject buildExistsObj(ElasticExistsFilter existsFilter) {
        JsonObject valueObj = new JsonObject();

        valueObj.addProperty("field", existsFilter.field);

        return valueObj;
    }
}
