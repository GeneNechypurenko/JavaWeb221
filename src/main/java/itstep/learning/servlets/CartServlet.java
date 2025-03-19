package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.rest.RestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Singleton
public class CartServlet extends HttpServlet {
    private final DataContext dataContext;
    private final RestService restService;

    @Inject
    public CartServlet(DataContext dataContext, RestService restService) {
        this.dataContext = dataContext;
        this.restService = restService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}

/*
            АРІ авторизованого користувача на прикладі замовлень/кошику
            1) Структура даних
            [UserAccess]     [carts]              [cart_items]
            id --------\     cart_id  ------\     cart_item_id
                        \--- user_access_id  \--- cart_id
                             created_at           product_id -------  [products]
                             closed_a             cart_item_price
                             is_cancelled         action_id --------- [actions]
                             cart_price           quantity
*/
