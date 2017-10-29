package io.github.chrislo27.rhre3.entity.model.special

import com.badlogic.gdx.graphics.Color
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.chrislo27.rhre3.entity.model.IEditableText
import io.github.chrislo27.rhre3.entity.model.IStretchable
import io.github.chrislo27.rhre3.entity.model.ModelEntity
import io.github.chrislo27.rhre3.registry.datamodel.impl.special.Subtitle
import io.github.chrislo27.rhre3.registry.datamodel.impl.special.Subtitle.SubtitleType.SONG_ARTIST
import io.github.chrislo27.rhre3.registry.datamodel.impl.special.Subtitle.SubtitleType.SONG_TITLE
import io.github.chrislo27.rhre3.registry.datamodel.impl.special.Subtitle.SubtitleType.SUBTITLE
import io.github.chrislo27.rhre3.track.Remix


class SubtitleEntity(remix: Remix, datamodel: Subtitle)
    : ModelEntity<Subtitle>(remix, datamodel), IStretchable, IEditableText {

    override val isStretchable: Boolean = true
    var subtitle: String = ""
    override val renderText: String
        get() = "${datamodel.name}\n\"$subtitle[]\""
    override var text: String
        get() = subtitle
        set(value) {
            subtitle = value
        }

    init {
        bounds.width = 1f
        bounds.height = 1f
    }

    override fun saveData(objectNode: ObjectNode) {
        super.saveData(objectNode)
        objectNode.put("subtitle", subtitle)
    }

    override fun readData(objectNode: ObjectNode) {
        super.readData(objectNode)
        subtitle = objectNode["subtitle"].asText("<failed to read text>")
    }

    override fun getRenderColor(): Color {
        return remix.editor.theme.entities.special
    }

    override fun onStart() {
        when (datamodel.type) {
            SUBTITLE -> {
                if (this !in remix.currentSubtitles) {
                    remix.currentSubtitles += this
                }
            }
            SONG_TITLE -> remix.editor.songTitle(subtitle)
            SONG_ARTIST -> remix.editor.songArtist(subtitle)
        }
    }

    override fun whilePlaying() {
    }

    override fun onEnd() {
        when (datamodel.type) {
            SUBTITLE -> remix.currentSubtitles.remove(this)
            SONG_TITLE -> remix.editor.songTitle(null)
            SONG_ARTIST -> remix.editor.songArtist(null)
        }
    }

    override fun copy(remix: Remix): SubtitleEntity {
        return SubtitleEntity(remix, datamodel).also {
            it.updateBounds {
                it.bounds.set(this@SubtitleEntity.bounds)
            }
            it.subtitle = subtitle
        }
    }

}
