import com.example.movieapp.Movie;

import java.util.List;

public class SearchResponse {
    private List<Movie> Search;

    public List<Movie> getSearch() {
        return Search;
    }

    public void setSearch(List<Movie> search) {
        Search = search;
    }
}
