package me.whiteship.demoinfleanrestapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    /*@Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeFieldName("errors");
        gen.writeStartArray();
        errors.getFieldErrors().stream().forEach(e -> {

            try{
                gen.writeStartObject();
                gen.writeStringField("field" , e.getField());
                gen.writeStringField("objectName" , e.getObjectName());
                gen.writeStringField("code" , e.getCode());
                gen.writeStringField("defaultMessage" , e.getDefaultMessage());
                Object rejectedValue = e.getRejectedValue();
                if(rejectedValue != null){
                    gen.writeStringField("rejectedValue" , rejectedValue.toString());
                }
                gen.writeEndObject();
            }catch (IOException e1){
                e1.printStackTrace();
            }

        });

        errors.getGlobalErrors().forEach(e -> {
            try{
                gen.writeStartObject();
                gen.writeStringField("objectName" , e.getObjectName());
                gen.writeStringField("code" , e.getCode());
                gen.writeStringField("defaultMessage" , e.getDefaultMessage());
                gen.writeEndObject();
            }catch (IOException e1){
                e1.printStackTrace();
            }

        });
    }*/
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("errors");
        jsonGenerator.writeStartArray();
        errors.getFieldErrors().stream().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("field", e.getField());
                jsonGenerator.writeStringField("code" , e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());

                Object rejectedValue = e.getRejectedValue();
                if (rejectedValue != null) {
                    jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
                } else {
                    jsonGenerator.writeStringField("rejectedValue", "");
                }
                jsonGenerator.writeEndObject();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });
        jsonGenerator.writeEndArray();
    }

}
