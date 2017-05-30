package edu.iis.mto.staticmock;

import org.junit.Before;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import edu.iis.mto.staticmock.reader.NewsReader;
import static org.junit.Assert.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;



@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class})
public class NewsLoaderTest {
	private IncomingNews incomingNews = new IncomingNews();
	private NewsLoader newsLoader;
	IncomingInfo incomingInfoPublic;
	IncomingInfo incomingInfoForSubscribers;
	String readerType;
	
	@Before
	public void setUp() throws Exception {
		incomingInfoPublic = new IncomingInfo("public content", SubsciptionType.NONE);
		incomingInfoForSubscribers = new IncomingInfo("content for subsribers A", SubsciptionType.A);
		
        mockStatic(ConfigurationLoader.class);
        ConfigurationLoader mockConfigurationLoader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(mockConfigurationLoader);

        Configuration configuration = new Configuration();
        readerType = "File";
        Whitebox.setInternalState(configuration, "readerType", readerType);
        when(mockConfigurationLoader.loadConfiguration()).thenReturn(configuration);

        mockStatic(PublishableNews.class);
        when(PublishableNews.create()).thenReturn(new PublishableNewsForTests());

        incomingNews = new IncomingNews();
        incomingNews.add(incomingInfoPublic);
        incomingNews.add(incomingInfoForSubscribers);

        NewsReader newsReader = new NewsReader() {
            @Override
            public IncomingNews read() {
                return incomingNews;
            }
        };
        
        mockStatic(NewsReaderFactory.class);
        when(NewsReaderFactory.getReader(readerType)).thenReturn(newsReader);

        newsLoader = new NewsLoader();		
	}
	
	@Test
	public void verifyNewsSeparation() throws Exception {
		PublishableNewsForTests testablePublishableNews = (PublishableNewsForTests) newsLoader.loadNews();

        assertThat(testablePublishableNews.getPublicInfo(), hasItem(incomingInfoPublic.getContent()));
        assertThat(testablePublishableNews.getSubscriptionInfo(), not(hasItem(incomingInfoPublic.getContent())));
        assertThat(testablePublishableNews.getPublicInfo(), not(hasItem(incomingInfoForSubscribers.getContent())));
        assertThat(testablePublishableNews.getSubscriptionInfo(), hasItem(incomingInfoForSubscribers.getContent()));

	}
	@Test
	public void verifyIfDependencyIsTriggeredCorrectly() {
		newsLoader.loadNews();
		
		verifyStatic(times(1));
		NewsReaderFactory.getReader(readerType);	
	}
}
