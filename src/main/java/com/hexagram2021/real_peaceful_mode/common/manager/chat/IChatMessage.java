package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;

import javax.annotation.Nullable;
import java.util.List;

//Tree structure, usually link structure without selection.
public interface IChatMessage {
	String messageKey();
	Speaker speaker();

	@Nullable
	AbstractChatMessage getNext();
	@Nullable
	List<ChatSelection> getSelections();
}
