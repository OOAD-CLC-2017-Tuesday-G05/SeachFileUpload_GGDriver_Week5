package hello.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import googledrive.DriveService;

@Service
public class FileSearchService implements SearchService {

	@Autowired
	public FileSearchService() {

	}
	@Override
	public List<String> doSearch(String name) {
		// TODO Auto-generated method stub
		DriveService driveService = new DriveService();
		return driveService.doSearch(name);
	}

}
