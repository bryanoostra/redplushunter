package vs.plotagent.ui;

import vs.rationalagent.ui.StoryAgentEvent;

public class AgentSelectActionEvent extends StoryAgentEvent {
	
	private String m_characterURI;
	
	public AgentSelectActionEvent(Object src, String characterURI) {
		super(src);
		m_characterURI = characterURI;
	}
	
	public String getCharacterURI() {
		return m_characterURI;
	}
}
