package eccomerce.backend_eccomerce.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderEnum {
    MALE("masculino"),
    FEMALE("femenino"),
    OTHER("otro");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    // Método getter para obtener el valor personalizado
    @JsonValue
    public String getValue() {
        return value;
    }

    // Método estático para deserialización
    @JsonCreator
    public static GenderEnum fromValue(String value) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.value.equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender value: " + value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
