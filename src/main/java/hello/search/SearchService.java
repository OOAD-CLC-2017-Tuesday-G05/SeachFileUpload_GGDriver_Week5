package hello.search;

import com.google.api.services.drive.model.*;

public interface SearchService {
	String find(String name);
	void load(String path);
}
