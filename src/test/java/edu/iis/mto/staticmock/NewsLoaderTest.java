package edu.iis.mto.staticmock;

import org.junit.Before;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import edu.iis.mto.staticmock.reader.NewsReader;



@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class})
public class NewsLoaderTest {
	private IncomingNews incomingNews = new IncomingNews();
	
	@Before
	public void setUp() {
		IncomingInfo incomingInfoPublic = new IncomingInfo("public content", SubsciptionType.NONE);
		IncomingInfo incomingInfoForSubscribers = new IncomingInfo("content for subsribers A", SubsciptionType.A);
		
		
        mockStatic(ConfigurationLoader.class);
        ConfigurationLoader mockConfigurationLoader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(mockConfigurationLoader);

        Configuration configuration = new Configuration();
        String readerType = "File";
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

        NewsLoader newsLoader = new NewsLoader();		
	}
	
	@Test
	public void test() {

	}

}
