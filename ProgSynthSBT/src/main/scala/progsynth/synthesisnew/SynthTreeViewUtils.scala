package progsynth.synthesisnew

import scala.xml.Elem

import progsynth.utils.PSUtils._

trait SynthTreeViewUtils { self: SynthTree =>
//	def toXml: Elem = {
//		<tree>
//				{ rootNode.toXmlRec }
//				<curNodeId>{ curNode.id }</curNodeId>
//			</tree>
//	}

	def cssElem = <link rel="stylesheet" type="text/css" href="progsynthlog.css"> </link> ++
	<link rel="stylesheet" href="css/ui-lightness/jquery-ui-1.10.0.custom.css" />

	def jsElem: List[Elem] = {
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script> ::
		<script type="text/javascript" src="/site_media/js/jstree/jquery.jstree.js"></script> ::
		<script type="text/javascript" src="jquery.hotkeys.js"></script> ::
		<script type="text/javascript" src="jquery-ui-1.10.0.custom.js"></script> ::
		<script type="text/javascript" src="ps_derivation.js"></script> :: Nil
	}

	def topPaneElem: Elem = {
		<div class="top">
				<div class="topButton" id="toggleIdBtn"> Toggle Ids </div>
				<div class="topButton"> 2 </div>
				<div class="topButton"> 3 </div>
				<div class="topButton" id="optionsBtn"> Options </div>
			</div>
	}

	def treeToXhtml(): Elem = {
		<html>
				<head>
					{ cssElem }
					{ jsElem }
				</head>
				<body>
					<div class="left">
						{ rootNode.toXhtmlRec() }
						<div class="curNodeId">{ curNode.id }</div>
					</div>
					<div class="right">
						{topPaneElem}
						<div class="content"></div>
						<div class="statusbar">
							<div class="displayIdDiv">DisplayIdDiv</div>
						</div>
					</div>
				</body>
			</html>
	}

	var outputFile: String = null

	def setOutputFile(filePath: String) = {
		outputFile = filePath
	}

	//copy ps_derivation.js to output folder
	private def copyJSFile(outputDir: String) = {
		val srcOpt = getFullFilePath("/ps_derivation.js")
		srcOpt foreach { src =>
			val dest = outputDir + java.io.File.separator + "ps_derivation.js"
			copyFile(src, dest)
		}
	}

	private def copyCSSFile(outputDir: String) = {
		val srcOpt = getFullFilePath("/progsynthlog.css")
		srcOpt foreach { src =>
			val dest = outputDir + java.io.File.separator + "progsynthlog.css"
			copyFile(src, dest)
		}
	}

	def dumpState = {
		if (outputFile != null ) {
			//Generate the html and write it to output file.
			val htmlStr = treeToXhtml()
			overwriteFile(outputFile, htmlStr)
			//Copy the Javascript and CSS files to output folder
			val outputDir = getDirNameFromFullPath(outputFile)
			//copyJSFile(outputDir) //TODO: Disabled temporarily
			//copyCSSFile(outputDir) //TODO: Disabled temporarily
		}
	}

}