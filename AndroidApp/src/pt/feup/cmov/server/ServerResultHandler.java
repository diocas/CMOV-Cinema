package pt.feup.cmov.server;

public interface ServerResultHandler<T>
{
	void onServerResultSucess(T response, int httpStatusCode);
	
	void onServerResultFailure(Exception exception);
}
