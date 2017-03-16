package hello.search;

import java.util.List;

import com.google.api.services.drive.model.*;
import java.io.FilenameFilter;
public interface SearchService {
	//name= name.toString().trim();
	List<String> doSearch(String name);
	
	
}
