_writer.write("""<span""")

// add class attribute
_writer.write(""" class="special-span" """)

// write tag attributes
for (entry in _params) {
    _writer.write(""" ${entry.key}=\"${entry.value}\"""")
}

_writer.write(""">""")
if (_body != null) {
    // render inner body.
    _body.render(_writer)
}
_writer.write("""</span>""")
