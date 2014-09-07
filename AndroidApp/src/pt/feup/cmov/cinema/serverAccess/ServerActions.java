package pt.feup.cmov.cinema.serverAccess;

public enum ServerActions
{
	MoviesGet,
	ReservationGet,
	ReservationGetByUser,
	ReservationGetByUserUpdateDate,
	ReservationPost,
	ReservationDelete,
	SessionsGet,
	SessionsGetByMovie,
	SessionGetAvailableSeats,
	SessionGetAvailableSeatsCount,
	SessionGetAvailableSeatsCountList,
	SessionGetUnavailableSeatsList,
	SessionGetTotalSeatsCount,
	UserGetAccount,
	UserPost,
	UserPut;
	
	public static String ServiceLocation = "http://portatil-diogo:8080/cinemaServer/";

	public String getUrl()
	{
		return ServiceLocation + "arrabida20/" + getUrlTemplate();
	}
	
	private String getUrlTemplate()
	{
		switch (this)
		{
		case MoviesGet:
			return "movies/%s";
		case ReservationGet:
			return "reservations/%s";
		case ReservationGetByUser:
			return "reservations/user/%s";
		case ReservationGetByUserUpdateDate:
			return "reservations/user/%s/%s";
		case ReservationPost:
			return "reservations/";
		case ReservationDelete:
			return "reservations/%s";
		case SessionsGet:
			return "sessions/%s";
		case SessionsGetByMovie:
			return "sessions/movie/%s";
		case SessionGetAvailableSeats:
			return "sessions/seats/%s/%s/%s";
		case SessionGetAvailableSeatsCount:
			return "sessions/seats/%s/%s/%s/count";
		case SessionGetAvailableSeatsCountList:
			return "sessions/seats/%s/%s/list/count";
		case SessionGetUnavailableSeatsList:
			return "sessions/seats/%s/%s";
		case SessionGetTotalSeatsCount:
			return "sessions/seats/%s/%s/count";
		case UserGetAccount:
			return "users/email/%s";
		case UserPost:
			return "users/new/";
		case UserPut:
			return "users/%s";
		default:
			return "";
		}
	}
}