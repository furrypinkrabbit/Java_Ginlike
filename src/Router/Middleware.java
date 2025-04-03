package Router;

public interface Middleware {
    RouteResult apply(RouteHandler handler, RequestMethod method, Request request);
}