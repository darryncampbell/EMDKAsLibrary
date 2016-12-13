# EMDKAsLibrary
Proof of concept demonstration to wrap the EMDK in an Android ARchive

## Overview
This sample is based on https://github.com/Zebra/samples-emdkforandroid-6_0/tree/BarcodeSample1 and demonstrates how to wrap Zebra's Enterprise Mobility Development Kit (EMDK) in an Android archive file (.aar).  The reason to do this is to make the implementation portable across teams and encapsulate the scanning logic.

## Explanation
This solution consists of two projects:
* app: The UI (copy / pasted from https://github.com/Zebra/samples-emdkforandroid-6_0/tree/BarcodeSample1).  Makes calls to emdkaar to scan
* emdkaar: The android archive containing all the scanning logic of the EMDK.  Provides bi-directional communication which is closely tied to the UI.  Obviously in a real implementation the UI would not be so closely tied to the scanning logic but this is a proof of concept.  Again the scanning logic is based heavily on https://github.com/Zebra/samples-emdkforandroid-6_0/tree/BarcodeSample1.  

Note: There are two classes:
- EMDKWrapper: A public class that exposes a simple scanning interface and communicates back to the main app through IEMDKWrapperCommunication
- EMDKImplementationWrapper: A private class that encapsulates the EMDK library.  This was done to avoid the main application having to have any knowledge of the EMDK scanning library.