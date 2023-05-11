/*
 * Copyright 2023 Tomas Toth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.kryom.wallets_backend.web.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import xyz.kryom.wallets_backend.web.BindingErrorsResponse;

/**
 * @author Tomas Toth
 */

@ControllerAdvice
public class ExceptionAdvice {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exception(Exception e){
    ErrorInfo errorInfo = new ErrorInfo(e.getLocalizedMessage());
    try {
      String errorJson = OBJECT_MAPPER.writeValueAsString(errorInfo);
      return ResponseEntity.badRequest().body(errorJson);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<Void> handleBadArgumentException(MethodArgumentNotValidException ex){

    BindingErrorsResponse bindingErrorsResponse = new BindingErrorsResponse();
    HttpHeaders headers = new HttpHeaders();
    BindingResult bindingResult = ex.getBindingResult();
    if (bindingResult.hasErrors()) {
      bindingErrorsResponse.addAllErrors(bindingResult);
      headers.add("errors", bindingErrorsResponse.toJSON());
    }
    return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
  }

  @Getter
  @Setter
  private static class ErrorInfo {
    public final String error;

    public ErrorInfo(String message){
      this.error = message;
    }
  }

}
