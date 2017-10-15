
java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/church_GO*/Folder/church_GO.arff   -verbosity 5    -x 10 > ./resault/church_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/derisi_GO*/Folder/derisi_GO.arff   -verbosity 5    -x 10 > ./resault/derisi_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/eisen_GO*/Folder/eisen_GO.arff   -verbosity 5    -x 10 > ./resault/eisen_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/gasch1_GO*/Folder/gasch1_GO.arff   -verbosity 5    -x 10 > ./resault/gasch1_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/gasch2_GO*/Folder/gasch2_GO.arff   -verbosity 5    -x 10 > ./resault/gasch2_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/pheno_GO*/Folder/pheno_GO.arff   -verbosity 5    -x 10 > ./resault/pheno_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/seq_GO*/Folder/seq_GO.arff   -verbosity 5    -x 10 > ./resault/seq_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/spo_GO*/Folder/spo_GO.arff   -verbosity 5    -x 10 > ./resault/spo_GO_CC_result.txt;

java -cp "./target/sources/*" meka.classifiers.multilabel.CC -t ./target/data/datasets_GO/church_GO*/Folder/church_GO.arff   > ./resault/a.txt;