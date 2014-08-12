package ameba.aeromock.util

import java.io.Writer

/**
 * Dummy writer object.
 * @author stormcat24
 */
object DummyWriter extends Writer {

  /**
   * @inheritdoc
   */
  override def write(cbuf: Array[Char], off: Int, len: Int): Unit = {}

  /**
   * @inheritdoc
   */
  override def flush(): Unit = {}

  /**
   * @inheritdoc
   */
  override def close(): Unit = {}
}
