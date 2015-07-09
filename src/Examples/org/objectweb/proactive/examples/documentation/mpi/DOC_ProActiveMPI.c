//@snippet-start ProActiveMPI
#include "ProActiveMPI.h"

/**
 * ProActiveMPI_Init
 * Init ProActiveMPI environment, bind the native process to the Java wrapping process.
 *
 * Input parameters
 * rank	- MPI rank of the process
 */
int ProActiveMPI_Init(int rank);

/**
 * ProActiveMPI_Finalize
 * Finalize ProActiveMPI infrastructure.
 */
int ProActiveMPI_Finalize();

/**
 * ProActiveMPI_Job
 * Finalize ProActiveMPI infrastructure.
 *
 * Output parameters
 * job			- the number of coupled independent MPI applications
 * nb_process	- the global number of processes involved in the coupled application
 */
int ProActiveMPI_Job(int * job, int * nb_process);


/**
 * ProActiveMPI_Send
 * Sends data to a remote process, possibly running on a different MPI application.
 *
 * Input Parameters
 * buf      - the buffer to be sent
 * count    - number of elements in send buffer (nonnegative integer)
 * datatype - datatype of each recv buffer element
 * dest     - rank of destination (integer)
 * tag      - message tag (integer)
 * jobID    - remote job (integer)
 * request	- communication request (handle)
 */
int ProActiveMPI_Send(void* buf, int count, MPI_Datatype datatype, int dest, int tag, int idjob );

/**
 * ProActiveMPI_Recv
 * Performs a blocking receive waiting from data from ProActive java side (eventually dispatched from a distant MPI process)
 *
 * Output Parameters
 * buf	- initial address of receive buffer
 *
 * Input Parameters
 * count	- number of elements in send buffer (nonnegative integer)
 * datatype	- datatype of each recv buffer element
 * src		- rank of source (integer)
 * tag		- message tag (integer)
 * jobID	- remote job (integer)
 */
int ProActiveMPI_Recv(void *buf, int count, MPI_Datatype datatype, int src, int tag, int jobID);


/**
 * ProActiveMPI_IRecv
 * Performs a non blocking receive from MPI side to receive data from ProActive java side (eventually dispatched from a distant MPI process)
 *
 * Output Parameters
 * request	- communication request (handle)
 *
 * Input Parameters
 * buf		- initial address of receive buffer
 * count	- number of elements in send buffer (nonnegative integer)
 * datatype	- datatype of each recv buffer element
 * src		- rank of source (integer)
 * tag		- message tag (integer)
 * jobID	- remote job (integer)
 */
int ProActiveMPI_IRecv(void* buf, int count, MPI_Datatype datatype, int src, int tag, int idjob, ProActiveMPI_Request * request);


/**
 * ProActiveMPI_Bcast
 * Performs a broadcast to an group of processe
 *
 * Input Parameters
 * sendbuf			- initial address of sender buffer
 * count			- number of elements in send buffer (nonnegative integer)
 * datatype			- datatype of each recv buffer element
 * nb_send			- the ID of the sender
 * tag				- message tag (integer)
 * jobID			- remote job (integer)
 * pa_rank_array	- array of destinations
 */
int ProActiveMPI_Bcast(void * sendbuf, int count, MPI_Datatype datatype, int tag, int nb_send, int * pa_rank_array);

/**
 * ProActiveMPI_Scatter
 * Scatter a set of buffers to a set of destinations
 *
 * Input Parameters
 * buffers			- array of pointers to buffers
 * count			- array of integer containing number of elements in each send buffer (nonnegative integers)
 * datatype			- datatype of each recv buffer element
 * tag				- message tag (integer)
 * pa_rank_array	- array of destinations
 */
int ProActiveMPI_Scatter(int nb_send, void ** buffers, int * count_array, MPI_Datatype datatype, int tag, int * pa_rank_array);

/**
 * ProActiveMPI_Test
 * Tests for the completion of receive from a ProActive java class
 *
 * Output Parameters
 * flag	- true if operation completed (logical)
 *
 * Input Parameters
 * request	- communication request (handle)
 */
int ProActiveMPI_Test(ProActiveMPI_Request *request, int *flag);

/**
 * ProActiveMPI_Wait
 * Waits for an MPI receive from a ProActive java class to complete
 *
 * Input Parameters
 * request	- communication request (handle)
 */
int ProActiveMPI_Wait(ProActiveMPI_Request *request);
//@snippet-end ProActiveMPI

