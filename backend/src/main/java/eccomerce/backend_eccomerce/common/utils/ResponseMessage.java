package eccomerce.backend_eccomerce.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage<T> {
  private int statusCode;
  private String message;
  private String error;
  private T data;
  private Integer countData;

   // Constructor vacío
   public ResponseMessage() {
  }

  // Constructor completo
  public ResponseMessage(int statusCode, String message, String error, T data, Integer countData) {
      this.statusCode = statusCode;
      this.message = message;
      this.error = error;
      this.data = data;
      this.countData = countData;
  }

  // Getters y Setters

  public int getStatusCode() {
      return statusCode;
  }

  public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
  }

  public String getMessage() {
      return message;
  }

  public void setMessage(String message) {
      this.message = message;
  }

  public String getError() {
      return error;
  }

  public void setError(String error) {
      this.error = error;
  }

  public T getData() {
      return data;
  }

  public void setData(T data) {
      this.data = data;
  }

  public Integer getCountData() {
      return countData;
  }

  public void setCountData(Integer countData) {
      this.countData = countData;
  }

  // Métodos para construir respuestas fácilmente
  public static <T> ResponseMessage<T> success(T data, String message, Integer countData) {
      return new ResponseMessage<>(200, message, null, data, countData);
  }

  public static <T> ResponseMessage<T> error(String errorMessage, int statusCode) {
      return new ResponseMessage<>(statusCode, "Error en la solicitud", errorMessage, null, null);
  }

  public static <T> ResponseMessage<T> error(String message, String errorMessage, int statusCode) {
      return new ResponseMessage<>(statusCode, message, errorMessage, null, null);
  }

  public static <T> ResponseMessage<T> ok(String message, int statusCode)
  {
      return new ResponseMessage<>(statusCode, message, null, null, null);
  }
}