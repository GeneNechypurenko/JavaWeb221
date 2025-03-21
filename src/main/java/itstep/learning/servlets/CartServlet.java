package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.Cart;
import itstep.learning.dal.dto.Product;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

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
        RestResponse restResponse = new RestResponse()
                .setResourceUrl("POST /cart")
                .setCacheTime(600)
                .setMetadata(Map.of(
                        "dataType", "string",
                        "read", "GET /cart",
                        "update", "PUT /cart",
                        "delete", "DELETE /cart"
                ));

        UserAccess userAccess = (UserAccess) req.getAttribute("authUserAccess");

        if(userAccess == null) {
            restService.sendJson(resp, restResponse.setStatus(401).setData(req.getAttribute("authStatus")));
            return;
        }

        UUID productId;
        try{
           productId = UUID.fromString(req.getParameter("productId"));
        }catch (Exception ignored) {
            restService.sendJson(resp, restResponse.setStatus(400).setData("Not a valid productId"));
            return;
        }

        Product product = dataContext.getProductDao().getProductById(productId);
        if(product == null) {
            restService.sendJson(resp, restResponse.setStatus(404).setData("Product not found"));
            return;
        }

        Cart cart = dataContext.getCartDao().getUserCart(userAccess.getUserAccessId(), true);
        if(dataContext.getCartDao().addToCart(cart, product)) {
            cart = dataContext.getCartDao().getCart(cart.getCartId());
            restService.sendJson(resp, restResponse.setStatus(202).setData(cart));
        }else {
            restService.sendJson(resp, restResponse.setStatus(500).setData("See server logs"));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        restService.setCorsHeader(resp);
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
