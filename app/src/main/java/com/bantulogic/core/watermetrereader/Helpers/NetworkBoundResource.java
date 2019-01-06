package com.bantulogic.core.watermetrereader.Helpers;

import com.bantulogic.core.watermetrereader.Data.DataSource.NetworkRESTAPI.ApiResponse;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

/** NOTES
 *
 * @param <ResultType>
 * @param <RequestType>
 *
 * It starts by observing the database for the resource. When the entry is loaded from the database
 * for the first time, NetworkBoundResource checks whether the result is good enough to be
 * dispatched or that it should be re-fetched from the network. Note that both of these situations
 * can happen at the same time, given that you probably want to show cached data while updating it
 * from the network.
 *
 * If the network call completes successfully, it saves the response into the database and
 * re-initializes the stream. If network request fails, the NetworkBoundResource dispatches a
 * failure directly.
 *
 * Note: After saving new data to disk, we re-initialize the stream from the database. We usually
 * don't need to do that, however, because the database itself happens to dispatch the change.
 *
 * Keep in mind that relying on the database to dispatch the change involves relying on the
 * associated side effects, which isn't good because undefined behavior from these side effects
 * could occur if the database ends up not dispatching changes because the data hasn't changed.
 *
 * Also, don't dispatch the result that arrived from the network because that would violate the
 * single source of truth principle. After all, maybe the database includes triggers that change
 * data values during a "save" operation. Similarly, don't dispatch SUCCESS without the new data,
 * because then the client receives the wrong version of the data.
 *
 * The following code snippet shows the public API provided by NetworkBoundResource class for its
 * children.
 *
 * Note these important details about the class's definition:
 *
 * It defines two type parameters, ResultType and RequestType, because the data type returned from
 * the API might not match the data type used locally.
 * It uses a class called ApiResponse for network requests. ApiResponse is a simple wrapper around
 * the Retrofit2.Call class that convert responses to instances of LiveData.
 */

// ResultType: Type for the Resource data.
// RequestType: Type for the API response.
public abstract class NetworkBoundResource<ResultType, RequestType> {
    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database.
    @NonNull @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected abstract void onFetchFailed();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public abstract LiveData<Resource<ResultType>> getAsLiveData();
}
