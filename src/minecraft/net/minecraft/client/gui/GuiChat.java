package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import fr.bretzel.wedit.CommandManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

public class GuiChat extends GuiScreen
{
    private static final Pattern field_208608_i = Pattern.compile("(\\s+)");
    private String historyBuffer = "";

    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;

    /** Chat entry field */
    protected GuiTextField inputField;

    /**
     * is the text that appears when you press the chat key and the input box appears pre-filled
     */
    private String defaultInputFieldText = "";
    protected final List<String> field_195136_f = Lists.<String>newArrayList();
    protected int field_195138_g;
    protected int field_195140_h;
    private ParseResults<ISuggestionProvider> field_195135_u;
    private CompletableFuture<Suggestions> field_195137_v;
    private GuiChat.SuggestionsList field_195139_w;
    private boolean field_195141_x;
    private boolean field_211139_z;

    public GuiChat()
    {
    }

    public GuiChat(String defaultText)
    {
        this.defaultInputFieldText = defaultText;
    }

    @Nullable
    public IGuiEventListener getFocused()
    {
        return this.inputField;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void initGui()
    {
        this.mc.keyboardListener.func_197967_a(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
        this.inputField.func_195607_a(this::func_502227_a);
        this.inputField.func_195609_a(this::func_195128_a);
        this.guiEventListeners.add(this.inputField);
        this.func_195129_h();
    }

    /**
     * Called when the GUI is resized in order to update the world and the resolution
     */
    public void onResize(Minecraft mcIn, int w, int h)
    {
        String s = this.inputField.getText();
        this.setWorldAndResolution(mcIn, w, h);
        this.func_208604_b(s);
        this.func_195129_h();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        this.mc.keyboardListener.func_197967_a(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.inputField.updateCursorCounter();
    }

    private void func_195128_a(int p_195128_1_, String p_195128_2_)
    {
        String s = this.inputField.getText();
        this.field_195141_x = !s.equals(this.defaultInputFieldText);
        this.func_195129_h();
    }

    public boolean keyPressed(int key, int action, int mods)
    {
        if (this.field_195139_w != null && this.field_195139_w.func_198503_b(key, action, mods))
        {
            return true;
        }
        else if (key == 256)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            return true;
        }
        else if (key != 257 && key != 335)
        {
            if (key == 265)
            {
                this.getSentHistory(-1);
                return true;
            }
            else if (key == 264)
            {
                this.getSentHistory(1);
                return true;
            }
            else if (key == 266)
            {
                this.mc.ingameGUI.getChatGUI().func_194813_a((double)(this.mc.ingameGUI.getChatGUI().getLineCount() - 1));
                return true;
            }
            else if (key == 267)
            {
                this.mc.ingameGUI.getChatGUI().func_194813_a((double)(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1));
                return true;
            }
            else
            {
                if (key == 258)
                {
                    this.field_195141_x = true;
                    this.func_195131_X_();
                }

                return this.inputField.keyPressed(key, action, mods);
            }
        }
        else
        {
            String s = this.inputField.getText().trim();

            
            /**
             * Wedit Start
             */

            if (!s.isEmpty() && s.charAt(0) == '#')
            {
                CommandManager.processCommand(Minecraft.getMinecraft().player, s);
                this.mc.ingameGUI.getChatGUI().addToSentMessages(s);
                this.mc.displayGuiScreen((GuiScreen)null);
                return true;
            }

            /**
             * Wedit End
             */
            
            
            if (!s.isEmpty())
            {
                this.sendChatMessage(s);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
            return true;
        }
    }

    public void func_195131_X_()
    {
        if (this.field_195137_v != null && this.field_195137_v.isDone())
        {
            int i = 0;
            Suggestions suggestions = this.field_195137_v.join();

            if (!suggestions.getList().isEmpty())
            {
                for (Suggestion suggestion : suggestions.getList())
                {
                    i = Math.max(i, this.fontRenderer.getStringWidth(suggestion.getText()));
                }

                int j = MathHelper.clamp(this.inputField.func_195611_j(suggestions.getRange().getStart()), 0, this.width - i);
                this.field_195139_w = new GuiChat.SuggestionsList(j, this.height - 12, i, suggestions);
            }
        }
    }

    private static int func_208603_a(String p_208603_0_)
    {
        if (Strings.isNullOrEmpty(p_208603_0_))
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (Matcher matcher = field_208608_i.matcher(p_208603_0_); matcher.find(); i = matcher.end())
            {
                ;
            }

            return i;
        }
    }

    private void func_195129_h()
    {
        this.field_195135_u = null;

        if (!this.field_211139_z)
        {
            this.inputField.func_195612_c((String)null);
            this.field_195139_w = null;
        }

        this.field_195136_f.clear();
        String s = this.inputField.getText();
        StringReader stringreader = new StringReader(s);

        if (stringreader.canRead() && stringreader.peek() == '/')
        {
            stringreader.skip();
            CommandDispatcher<ISuggestionProvider> commanddispatcher = this.mc.player.connection.func_195515_i();
            this.field_195135_u = commanddispatcher.parse(stringreader, this.mc.player.connection.func_195513_b());

            if (this.field_195139_w == null || !this.field_211139_z)
            {
                StringReader stringreader1 = new StringReader(s.substring(0, Math.min(s.length(), this.inputField.getCursorPosition())));

                if (stringreader1.canRead() && stringreader1.peek() == '/')
                {
                    stringreader1.skip();
                    ParseResults<ISuggestionProvider> parseresults = commanddispatcher.parse(stringreader1, this.mc.player.connection.func_195513_b());
                    this.field_195137_v = commanddispatcher.getCompletionSuggestions(parseresults);
                    this.field_195137_v.thenRun(() ->
                    {
                        if (this.field_195137_v.isDone())
                        {
                            this.func_195133_i();
                        }
                    });
                }
            }
        }
        else
        {
            int i = func_208603_a(s);
            Collection<String> collection = this.mc.player.connection.func_195513_b().func_197011_j();
            this.field_195137_v = ISuggestionProvider.func_197005_b(collection, new SuggestionsBuilder(s, i));
        }
    }

    private void func_195133_i()
    {
        if (((Suggestions)this.field_195137_v.join()).isEmpty() && !this.field_195135_u.getExceptions().isEmpty() && this.inputField.getCursorPosition() == this.inputField.getText().length())
        {
            int i = 0;

            for (Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.field_195135_u.getExceptions().entrySet())
            {
                CommandSyntaxException commandsyntaxexception = entry.getValue();

                if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect())
                {
                    ++i;
                }
                else
                {
                    this.field_195136_f.add(commandsyntaxexception.getMessage());
                }
            }

            if (i > 0)
            {
                this.field_195136_f.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
        }

        this.field_195138_g = 0;
        this.field_195140_h = this.width;

        if (this.field_195136_f.isEmpty())
        {
            this.func_195132_a(TextFormatting.GRAY);
        }

        this.field_195139_w = null;

        if (this.field_195141_x && this.mc.gameSettings.autoSuggestions)
        {
            this.func_195131_X_();
        }
    }

    private String func_502227_a(String p_502227_1_, int p_502227_2_)
    {
        return this.field_195135_u != null ? func_502228_a(this.field_195135_u, p_502227_1_, p_502227_2_) : p_502227_1_;
    }

    public static String func_502228_a(ParseResults<ISuggestionProvider> p_502228_0_, String p_502228_1_, int p_502228_2_)
    {
        TextFormatting[] atextformatting = new TextFormatting[] {TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD};
        String s = TextFormatting.GRAY.toString();
        StringBuilder stringbuilder = new StringBuilder(s);
        int i = 0;
        int j = -1;
        CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = p_502228_0_.getContext().getLastChild();

        for (ParsedArgument < ISuggestionProvider, ? > parsedargument : commandcontextbuilder.getArguments().values())
        {
            ++j;

            if (j >= atextformatting.length)
            {
                j = 0;
            }

            int k = Math.max(parsedargument.getRange().getStart() - p_502228_2_, 0);

            if (k >= p_502228_1_.length())
            {
                break;
            }

            int l = Math.min(parsedargument.getRange().getEnd() - p_502228_2_, p_502228_1_.length());

            if (l > 0)
            {
                stringbuilder.append((CharSequence)p_502228_1_, i, k);
                stringbuilder.append((Object)atextformatting[j]);
                stringbuilder.append((CharSequence)p_502228_1_, k, l);
                stringbuilder.append(s);
                i = l;
            }
        }

        if (p_502228_0_.getReader().canRead())
        {
            int i1 = Math.max(p_502228_0_.getReader().getCursor() - p_502228_2_, 0);

            if (i1 < p_502228_1_.length())
            {
                int j1 = Math.min(i1 + p_502228_0_.getReader().getRemainingLength(), p_502228_1_.length());
                stringbuilder.append((CharSequence)p_502228_1_, i, i1);
                stringbuilder.append((Object)TextFormatting.RED);
                stringbuilder.append((CharSequence)p_502228_1_, i1, j1);
                i = j1;
            }
        }

        stringbuilder.append((CharSequence)p_502228_1_, i, p_502228_1_.length());
        return stringbuilder.toString();
    }

    public boolean mouseScrolled(double p_mouseScrolled_1_)
    {
        if (p_mouseScrolled_1_ > 1.0D)
        {
            p_mouseScrolled_1_ = 1.0D;
        }

        if (p_mouseScrolled_1_ < -1.0D)
        {
            p_mouseScrolled_1_ = -1.0D;
        }

        if (this.field_195139_w != null && this.field_195139_w.func_198498_a(p_mouseScrolled_1_))
        {
            return true;
        }
        else
        {
            if (!isShiftKeyDown())
            {
                p_mouseScrolled_1_ *= 7.0D;
            }

            this.mc.ingameGUI.getChatGUI().func_194813_a(p_mouseScrolled_1_);
            return true;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.field_195139_w != null && this.field_195139_w.func_198499_a((int)mouseX, (int)mouseY, button))
        {
            return true;
        }
        else
        {
            if (button == 0)
            {
                ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().func_194817_a(mouseX, mouseY);

                if (itextcomponent != null && this.handleComponentClick(itextcomponent))
                {
                    return true;
                }
            }

            return this.inputField.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(newChatText);
        }
        else
        {
            this.inputField.writeText(newChatText);
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.field_195139_w = null;
                this.sentHistoryCursor = i;
                this.field_195141_x = false;
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.func_195608_a(mouseX, mouseY, partialTicks);

        if (this.field_195139_w != null)
        {
            this.field_195139_w.func_198500_a(mouseX, mouseY);
        }
        else
        {
            int i = 0;

            for (String s : this.field_195136_f)
            {
                drawRect(this.field_195138_g - 1, this.height - 14 - 13 - 12 * i, this.field_195138_g + this.field_195140_h + 1, this.height - 2 - 13 - 12 * i, -16777216);
                this.fontRenderer.drawStringWithShadow(s, (float)this.field_195138_g, (float)(this.height - 14 - 13 + 2 - 12 * i), -1);
                ++i;
            }
        }

        ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().func_194817_a((double)mouseX, (double)mouseY);

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
        {
            this.handleComponentHover(itextcomponent, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void func_195132_a(TextFormatting p_195132_1_)
    {
        CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.field_195135_u.getContext();
        CommandContextBuilder<ISuggestionProvider> commandcontextbuilder1 = commandcontextbuilder.getLastChild();

        if (!commandcontextbuilder1.getNodes().isEmpty())
        {
            CommandNode<ISuggestionProvider> commandnode;
            int i;

            if (this.field_195135_u.getReader().canRead())
            {
                Entry<CommandNode<ISuggestionProvider>, StringRange> entry = (Entry)Iterables.getLast(commandcontextbuilder1.getNodes().entrySet());
                commandnode = entry.getKey();
                i = ((StringRange)entry.getValue()).getEnd() + 1;
            }
            else if (commandcontextbuilder1.getNodes().size() > 1)
            {
                Entry<CommandNode<ISuggestionProvider>, StringRange> entry2 = (Entry)Iterables.get(commandcontextbuilder1.getNodes().entrySet(), commandcontextbuilder1.getNodes().size() - 2);
                commandnode = entry2.getKey();
                i = ((StringRange)entry2.getValue()).getEnd() + 1;
            }
            else
            {
                if (commandcontextbuilder == commandcontextbuilder1 || commandcontextbuilder1.getNodes().isEmpty())
                {
                    return;
                }

                Entry<CommandNode<ISuggestionProvider>, StringRange> entry3 = (Entry)Iterables.getLast(commandcontextbuilder1.getNodes().entrySet());
                commandnode = entry3.getKey();
                i = ((StringRange)entry3.getValue()).getEnd() + 1;
            }

            Map<CommandNode<ISuggestionProvider>, String> map = this.mc.player.connection.func_195515_i().getSmartUsage(commandnode, this.mc.player.connection.func_195513_b());
            List<String> list = Lists.<String>newArrayList();
            int j = 0;

            for (Entry<CommandNode<ISuggestionProvider>, String> entry1 : map.entrySet())
            {
                if (!(entry1.getKey() instanceof LiteralCommandNode))
                {
                    list.add(p_195132_1_ + (String)entry1.getValue());
                    j = Math.max(j, this.fontRenderer.getStringWidth(entry1.getValue()));
                }
            }

            if (!list.isEmpty())
            {
                this.field_195136_f.addAll(list);
                this.field_195138_g = MathHelper.clamp(this.inputField.func_195611_j(i) + this.fontRenderer.getStringWidth(" "), 0, this.width - j);
                this.field_195140_h = j;
            }
        }
    }

    @Nullable
    private static String func_208602_b(String p_208602_0_, String p_208602_1_)
    {
        return p_208602_1_.startsWith(p_208602_0_) ? p_208602_1_.substring(p_208602_0_.length()) : null;
    }

    private void func_208604_b(String p_208604_1_)
    {
        this.inputField.setText(p_208604_1_);
    }

    class SuggestionsList
    {
        private final Rectangle2d field_198505_b;
        private final Suggestions field_198506_c;
        private final String field_208616_d;
        private int field_198507_d;
        private int field_198508_e;
        private Vec2f field_198509_f;
        private boolean field_199880_h;

        private SuggestionsList(int p_i47700_2_, int p_i47700_3_, int p_i47700_4_, Suggestions p_i47700_5_)
        {
            this.field_198509_f = Vec2f.ZERO;
            this.field_198505_b = new Rectangle2d(p_i47700_2_ - 1, p_i47700_3_ - 3 - Math.min(p_i47700_5_.getList().size(), 10) * 12, p_i47700_4_ + 1, Math.min(p_i47700_5_.getList().size(), 10) * 12);
            this.field_198506_c = p_i47700_5_;
            this.field_208616_d = GuiChat.this.inputField.getText();
            this.func_199675_a(0);
        }

        public void func_198500_a(int p_198500_1_, int p_198500_2_)
        {
            int i = Math.min(this.field_198506_c.getList().size(), 10);
            int j = -5592406;
            boolean flag = this.field_198507_d > 0;
            boolean flag1 = this.field_198506_c.getList().size() > this.field_198507_d + i;
            boolean flag2 = flag || flag1;
            boolean flag3 = this.field_198509_f.x != (float)p_198500_1_ || this.field_198509_f.y != (float)p_198500_2_;

            if (flag3)
            {
                this.field_198509_f = new Vec2f((float)p_198500_1_, (float)p_198500_2_);
            }

            if (flag2)
            {
                Gui.drawRect(this.field_198505_b.func_199318_a(), this.field_198505_b.func_199319_b() - 1, this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c(), this.field_198505_b.func_199319_b(), -805306368);
                Gui.drawRect(this.field_198505_b.func_199318_a(), this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d(), this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c(), this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d() + 1, -805306368);

                if (flag)
                {
                    for (int k = 0; k < this.field_198505_b.func_199316_c(); ++k)
                    {
                        if (k % 2 == 0)
                        {
                            Gui.drawRect(this.field_198505_b.func_199318_a() + k, this.field_198505_b.func_199319_b() - 1, this.field_198505_b.func_199318_a() + k + 1, this.field_198505_b.func_199319_b(), -1);
                        }
                    }
                }

                if (flag1)
                {
                    for (int i1 = 0; i1 < this.field_198505_b.func_199316_c(); ++i1)
                    {
                        if (i1 % 2 == 0)
                        {
                            Gui.drawRect(this.field_198505_b.func_199318_a() + i1, this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d(), this.field_198505_b.func_199318_a() + i1 + 1, this.field_198505_b.func_199319_b() + this.field_198505_b.func_199317_d() + 1, -1);
                        }
                    }
                }
            }

            boolean flag4 = false;

            for (int l = 0; l < i; ++l)
            {
                Suggestion suggestion = (Suggestion)this.field_198506_c.getList().get(l + this.field_198507_d);
                Gui.drawRect(this.field_198505_b.func_199318_a(), this.field_198505_b.func_199319_b() + 12 * l, this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c(), this.field_198505_b.func_199319_b() + 12 * l + 12, -805306368);

                if (p_198500_1_ > this.field_198505_b.func_199318_a() && p_198500_1_ < this.field_198505_b.func_199318_a() + this.field_198505_b.func_199316_c() && p_198500_2_ > this.field_198505_b.func_199319_b() + 12 * l && p_198500_2_ < this.field_198505_b.func_199319_b() + 12 * l + 12)
                {
                    if (flag3)
                    {
                        this.func_199675_a(l + this.field_198507_d);
                    }

                    flag4 = true;
                }

                GuiChat.this.fontRenderer.drawStringWithShadow(suggestion.getText(), (float)(this.field_198505_b.func_199318_a() + 1), (float)(this.field_198505_b.func_199319_b() + 2 + 12 * l), l + this.field_198507_d == this.field_198508_e ? -256 : -5592406);
            }

            if (flag4)
            {
                Message message = ((Suggestion)this.field_198506_c.getList().get(this.field_198508_e)).getTooltip();

                if (message != null)
                {
                    GuiChat.this.drawHoveringText(TextComponentUtils.func_202465_a(message).getFormattedText(), p_198500_1_, p_198500_2_);
                }
            }
        }

        public boolean func_198499_a(int p_198499_1_, int p_198499_2_, int p_198499_3_)
        {
            if (!this.field_198505_b.func_199315_b(p_198499_1_, p_198499_2_))
            {
                return false;
            }
            else
            {
                int i = (p_198499_2_ - this.field_198505_b.func_199319_b()) / 12 + this.field_198507_d;

                if (i >= 0 && i < this.field_198506_c.getList().size())
                {
                    this.func_199675_a(i);
                    this.func_198501_a();
                }

                return true;
            }
        }

        public boolean func_198498_a(double p_198498_1_)
        {
            int i = (int)(GuiChat.this.mc.mouseHelper.getMouseX() * (double)GuiChat.this.mc.mainWindow.getWidthScaled() / (double)GuiChat.this.mc.mainWindow.getWindowWidth());
            int j = (int)(GuiChat.this.mc.mouseHelper.getMouseY() * (double)GuiChat.this.mc.mainWindow.getHeightScaled() / (double)GuiChat.this.mc.mainWindow.getWindowHeight());

            if (this.field_198505_b.func_199315_b(i, j))
            {
                this.field_198507_d = MathHelper.clamp((int)((double)this.field_198507_d - p_198498_1_), 0, Math.max(this.field_198506_c.getList().size() - 10, 0));
                return true;
            }
            else
            {
                return false;
            }
        }

        public boolean func_198503_b(int p_198503_1_, int p_198503_2_, int p_198503_3_)
        {
            if (p_198503_1_ == 265)
            {
                this.func_199879_a(-1);
                this.field_199880_h = false;
                return true;
            }
            else if (p_198503_1_ == 264)
            {
                this.func_199879_a(1);
                this.field_199880_h = false;
                return true;
            }
            else if (p_198503_1_ == 258)
            {
                if (this.field_199880_h)
                {
                    this.func_199879_a(GuiScreen.isShiftKeyDown() ? -1 : 1);
                }

                this.func_198501_a();
                return true;
            }
            else if (p_198503_1_ == 256)
            {
                this.func_198502_b();
                return true;
            }
            else
            {
                return false;
            }
        }

        public void func_199879_a(int p_199879_1_)
        {
            this.func_199675_a(this.field_198508_e + p_199879_1_);
            int i = this.field_198507_d;
            int j = this.field_198507_d + 10 - 1;

            if (this.field_198508_e < i)
            {
                this.field_198507_d = MathHelper.clamp(this.field_198508_e, 0, Math.max(this.field_198506_c.getList().size() - 10, 0));
            }
            else if (this.field_198508_e > j)
            {
                this.field_198507_d = MathHelper.clamp(this.field_198508_e + 1 - 10, 0, Math.max(this.field_198506_c.getList().size() - 10, 0));
            }
        }

        public void func_199675_a(int p_199675_1_)
        {
            this.field_198508_e = p_199675_1_;

            if (this.field_198508_e < 0)
            {
                this.field_198508_e += this.field_198506_c.getList().size();
            }

            if (this.field_198508_e >= this.field_198506_c.getList().size())
            {
                this.field_198508_e -= this.field_198506_c.getList().size();
            }

            Suggestion suggestion = (Suggestion)this.field_198506_c.getList().get(this.field_198508_e);
            GuiChat.this.inputField.func_195612_c(GuiChat.func_208602_b(GuiChat.this.inputField.getText(), suggestion.apply(this.field_208616_d)));
        }

        public void func_198501_a()
        {
            Suggestion suggestion = (Suggestion)this.field_198506_c.getList().get(this.field_198508_e);
            GuiChat.this.field_211139_z = true;
            GuiChat.this.func_208604_b(suggestion.apply(this.field_208616_d));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            GuiChat.this.inputField.setCursorPosition(i);
            GuiChat.this.inputField.setSelectionPos(i);
            this.func_199675_a(this.field_198508_e);
            GuiChat.this.field_211139_z = false;
            this.field_199880_h = true;
        }

        public void func_198502_b()
        {
            GuiChat.this.field_195139_w = null;
        }
    }
}
