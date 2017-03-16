package hello.search;

import java.util.List;

import com.google.api.services.drive.model.*;

public interface SearchService {
	List<String> doSearch(String name);
}
