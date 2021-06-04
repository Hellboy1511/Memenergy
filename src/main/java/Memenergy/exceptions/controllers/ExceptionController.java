package Memenergy.exceptions.controllers;

import Memenergy.data.ExceptionEntity;
import Memenergy.database.services.ExceptionEntityService;
import Memenergy.exceptions.exceptions.api.*;
import Memenergy.web.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    public void notFound() {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = UnauthorizedException.class)
    public void unauthorized() {
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = ForbiddenException.class)
    public void forbidden() {
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = ConflictException.class)
    public void conflict() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadRequestException.class)
    public void badRequest() {
    }
}
