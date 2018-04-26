package model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("ICE")
public class ICEUpload extends Upload {

	@Override
	public void importMarketDataReply() throws Exception {
		// TODO Auto-generated method stub
		
	}


}
