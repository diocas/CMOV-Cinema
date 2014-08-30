package pt.feup.cmov.cinema.serverAccess;

public interface ServerResultHandler<T>
{
	void onServerResultSucess(T response, int httpStatusCode);
	
	void onServerResultFailure(Exception exception);
}
