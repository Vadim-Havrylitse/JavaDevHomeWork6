package dto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

@NoArgsConstructor
public class DtoService {

    @SneakyThrows
    public <T> T convertResulSetToDto(ResultSet resultSet, Class<T> className){
        ObjectMapper mapper = new ObjectMapper();
        JSONObject jsonObj = new JSONObject();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (resultSet.next()) {
            jsonObj = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                String value = resultSet.getString(columnName);
                jsonObj.put(columnName, value);
            }
        }
        return mapper.readValue(jsonObj.toString(), className);
    }

    public <T> List<T> convertResulSetToDtoList(ResultSet resultSet, Class<T> className) throws SQLException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        JSONArray array = new JSONArray();
        while (resultSet.next()) {
            JSONObject jsonObj = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                String value = resultSet.getString(columnName);
                jsonObj.put(columnName, value);
            }
            array.put(jsonObj);
        }
        return mapper.readValue(array.toString(), new TypeReference<List<T>>() {
        });
    }

    public <T> T parseRequestToDto (HttpServletRequest req, Class<T> className) throws IOException {
        try {
            Constructor<T> constructor = className.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            for (Field field : className.getDeclaredFields()){
                field.setAccessible(true);
                Type type = field.getGenericType();
                if (type.getTypeName().contains("List")){
                    String[] parameterValues = req.getParameterValues(field.getName());
                    if (parameterValues == null){
                        continue;
                    }
                    List<String> parameterValue = List.of(parameterValues);
                    field.set(instance, parameterValue);
                } else {
                    String parameterValue = req.getParameter(field.getName());
                    if(parameterValue == null || parameterValue.isBlank()) {
                        continue;
                    }
                    field.set(instance, parseType(parameterValue, type));
                }
            }
            return instance;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IOException("parseRequestToDto() is fail!");
    }

    private Object parseType(Object value, Type type)
    {
        if(value.toString() == null) {
            return null;
        }
        Object result = null;
        if (String.class.equals(type))
        {
            result = value.toString();
        }
        else if (Byte.class.equals(type))
        {
            result = Byte.valueOf(value.toString());
        }
        else if (Integer.class.equals(type))
        {
            result = Integer.valueOf(value.toString());
        }
        else if (Short.class.equals(type))
        {
            result = Short.valueOf(value.toString());
        }
        else if (Long.class.equals(type))
        {
            result = Long.valueOf(value.toString());
        }
        else if (Float.class.equals(type))
        {
            result = Float.valueOf(value.toString());
        }
        else if (Double.class.equals(type))
        {
            result = Double.valueOf(value.toString());
        }
        else if (Boolean.class.equals(type))
        {
            result = Boolean.valueOf(value.toString());
        }
        return result;
    }

}
