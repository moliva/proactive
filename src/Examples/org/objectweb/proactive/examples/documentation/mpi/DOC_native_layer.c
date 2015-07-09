//@snippet-start native_layer
#include "native_layer.h"

/**
 * init
 * Start the handshake that binds the native and Java application
 *
 * Input Parameters
 * creation_flag	- Since the implementation of the IPC communication is shared by Java/Native,
 * 					  this flag identifies the origin of the function call
 * 					  (if native side,  creation_flag = 0 else creation_flag = 1)
 */
int init(int creation_flag);

/**
 * terminate
 * Terminate the wrapping, releasing memory and cleaning up running environment
 */
int terminate();

/**
 * recv_message
 * Receive message from Java wrapper
 *
 * Output Parameters
 * lenght	- size in bytes of the buffer that will be received
 * data_ptr	- pointer to the byte buffer
 */
int recv_message(int * length, void ** data_ptr);

/**
 * recv_message_async
 * Receive message asynchronously from Java wrapper
 *
 * Output Parameters
 * lenght	- size in bytes of the buffer that will be received
 * data_ptr	- pointer to the byte buffer
 */
int recv_message_async(int * length, void ** data_ptr);

/**
 * send_message
 * Send message to Java wrapper
 *
 * Output Parameters
 * lenght	- size in bytes of the buffer that will be received
 * data_ptr	- pointer to data structure that indicated the message to be sent (message includes data type, signature and buffer pointer)
 */
int send_message(int length, void *data_ptr);
//@snippet-end native_layer
