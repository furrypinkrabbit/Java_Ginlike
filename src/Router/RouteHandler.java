package Router;

public interface RouteHandler {
    // 仅保留这一个抽象方法
    RouteResult handle(RequestMethod method, Request request);
}
