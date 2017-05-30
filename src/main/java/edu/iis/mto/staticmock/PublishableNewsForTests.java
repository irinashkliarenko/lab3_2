package edu.iis.mto.staticmock;

import java.util.ArrayList;
import java.util.List;

public class PublishableNewsForTests extends PublishableNews{
	
	private final List<String> publicInfo = new ArrayList<>();
	private final List<String> subscriptionInfo = new ArrayList<>();
	
	
	@Override
	public void addPublicInfo(String content) {
		super.addPublicInfo(content);
		publicInfo.add(content);
	}
	
	@Override
	public void addForSubscription(String content, SubsciptionType subscriptionType) {
		super.addForSubscription(content, subscriptionType);
		subscriptionInfo.add(content);
	}
	
    public List<String> getSubscriptionInfo() {
        return subscriptionInfo;
    }

    public List<String> getPublicInfo() {
        return publicInfo;
    }

}
