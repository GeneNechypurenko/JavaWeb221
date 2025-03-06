package itstep.learning.services.form_parse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface FormParseService {
    FormParseResult parseRequest(HttpServletRequest req) throws IOException;
}
