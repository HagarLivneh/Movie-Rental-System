    #include <stdlib.h>
    #include <boost/locale.hpp>
	#include <boost/thread.hpp>
    #include "../include/connectionHandler.h"

    /**
    * This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
    */
	int readerFunc(ConnectionHandler* connectionHandler){

		    while (1) {

		        std::string answer;

		        if (!connectionHandler->getLine(answer)) {
		            break;
		        }

		        int len=answer.length();

		        answer.resize(len-1);
		        std::cout << answer << std::endl;
		        if (answer == "ACK signout succeeded") {
                    std::cout <<"Ready to exit. Press enter"<< std::endl;

                    break;
		        }
		    }

		    return 0;
    }
	int clientInput(ConnectionHandler* connectionHandler){

		    while (1) {
		        const short bufsize = 1024;
		        char buf[bufsize];
		        std::cin.getline(buf, bufsize);
		        std::string line(buf);
		        int len=line.length();
		        bool ans=connectionHandler->sendLine(line);

		        if ((!ans) || (len==0)) {
//		        if ((!ans)) {
		            break;
		        }
		    }
		    return 0;
	}
    int main (int argc, char *argv[]) {
    	boost::thread_group tgroup;
        if (argc < 3) {
            std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
            return -1;
        }
        std::string host = (argv[1]);
        short port = atoi(argv[2]);


        ConnectionHandler connectionHandler(host, port);
        if (!connectionHandler.connect()) {
            std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
            return 1;
        }
       boost::thread readerThread(readerFunc,&connectionHandler);
       boost::thread writerThread(clientInput,&connectionHandler);
       readerThread.join();
       writerThread.join();

       return 0;
    }


