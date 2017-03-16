package hello.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSearchService implements SearchService {

	@Autowired
	public FileSearchService() {

	}
	@Override
	public String doSearch(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(String path) {
		// TODO Auto-generated method stub
		
	}

}
