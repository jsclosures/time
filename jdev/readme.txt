<fieldType name="zen_text" class="solr.TextField" positionIncrementGap="100">
      <analyzer type="index">
        <tokenizer class="solr.StandardTokenizerFactory"/>
        <!-- in this example, we will only use synonyms at query time
        <filter class="solr.SynonymFilterFactory" synonyms="index_synonyms.txt" ignoreCase="true" expand="false"/>
        -->
        <!-- Case insensitive stop word removal.
        -->
        <filter class="solr.StopFilterFactory"
                ignoreCase="true"
                words="lang/stopwords_en.txt"
                />
        <filter class="solr.LowerCaseFilterFactory"/>
  <filter class="solr.EnglishPossessiveFilterFactory"/>
        <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
  <!-- Optionally you may want to use this less aggressive stemmer instead of PorterStemFilterFactory:
        <filter class="solr.EnglishMinimalStemFilterFactory"/>
  -->
        <filter class="solr.PorterStemFilterFactory"/>
            <similarity class="com.jsclosures.lucene.ScaledDisjunctionSimilarityFactory">
          </similarity>
      </analyzer>
      <analyzer type="query">
        <tokenizer class="solr.StandardTokenizerFactory"/>
        <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
        <filter class="solr.StopFilterFactory"
                ignoreCase="true"
                words="lang/stopwords_en.txt"
                />
        <filter class="solr.LowerCaseFilterFactory"/>
  <filter class="solr.EnglishPossessiveFilterFactory"/>
        <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
  <!-- Optionally you may want to use this less aggressive stemmer instead of PorterStemFilterFactory:
        <filter class="solr.EnglishMinimalStemFilterFactory"/>
  -->
        <filter class="solr.PorterStemFilterFactory"/>
            <similarity class="com.jsclosures.lucene.ScaledDisjunctionSimilarityFactory">
          </similarity>
      </analyzer>
    </fieldType>

    <fieldType name="terms_js" class="solr.TextField" positionIncrementGap="0">
      <analyzer type="index">
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" />
        <filter class="solr.LowerCaseFilterFactory"/>
      </analyzer>
      <analyzer type="query">
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" />
        <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
        <filter class="solr.LowerCaseFilterFactory"/>
      </analyzer>
     
    </fieldType>
    
    <fieldType name="terms_jsstem" class="solr.TextField" positionIncrementGap="0">
       <analyzer type="index">
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.ASCIIFoldingFilterFactory" preserveOriginal="false"/>
        <filter class="solr.StopFilterFactory"
                ignoreCase="true"
                words="lang/stopwords_en.txt"
                />
        <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="0" catenateWords="1" catenateNumbers="1" catenateAll="0" splitOnCaseChange="0" stemEnglishPossessive="1"/>
        <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
        <filter class="solr.PorterStemFilterFactory"/>
      </analyzer>
      <analyzer type="query">
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.ASCIIFoldingFilterFactory" preserveOriginal="false"/>
        <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
        <filter class="solr.StopFilterFactory"
                ignoreCase="true"
                words="lang/stopwords_en.txt"
                />
        <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="0" catenateWords="1" catenateNumbers="0" catenateAll="0" splitOnCaseChange="0" stemEnglishPossessive="1"/>
        <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
        <filter class="solr.PorterStemFilterFactory"/>
      </analyzer>
    </fieldType>

<field name="contenttype" type="string" indexed="true" stored="true" required="true" multiValued="false" /> 
<field name="sentencetype" type="string" indexed="true" stored="true" required="false" multiValued="false" /> 
<field name="action" type="string" indexed="true" stored="true" required="false" multiValued="false" />
<field name="condition" type="string" indexed="true" stored="true" required="false" multiValued="false" />  
<field name="grammars" type="string" indexed="true" stored="true" required="false" multiValued="true" /> 
<field name="responses" type="string" indexed="true" stored="true" required="false" multiValued="false" /> 
<field name="grammars_zen" type="zen_text" indexed="true" stored="true" required="false" multiValued="false" /> 
<field name="failedresponses" type="string" indexed="true" stored="true" required="false" multiValued="false" /> 
<field name="fields" type="string" indexed="true" stored="true" required="false" multiValued="false" />

<field name="contentowner" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="username" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="userkey" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="authname" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="authkey" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="useremail" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="useralias" type="string" indexed="true" stored="true" multiValued="false"/>
    
<field name="background" type="string" indexed="false" stored="true" multiValued="false"/>
<field name="title" type="terms_jsstem" indexed="true" stored="true" multiValued="false"/>

<field name="body" type="terms_js" indexed="true" stored="true" multiValued="false"/>
<field name="terms" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_noun" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_verb" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_adjective" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_adverb" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_nn" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_nns" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_nnp" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_nnps" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_np" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vbz" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vbp" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vb" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vp" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vbd" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vbg" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_vbn" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_jj" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_jjr" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_jjs" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_rb" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_rbr" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_rbs" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>
<field name="terms_tk" type="terms_jsstem" indexed="true" stored="true" multiValued="false" omitNorms="false" omitPositions="false" omitTermFreqAndPositions="false" termVectors="true"/>

<field name="parentid" type="string" indexed="true" stored="true" multiValued="false"/>
    
<field name="last_modified" type="date" indexed="true" stored="true" multiValued="false"/>
<field name="created" type="date" indexed="true" stored="true" multiValued="false"/>
<field name="timestamp" type="date"   indexed="true" stored="true" required="false"  multiValued="false"/>

<field name="name" type="terms_jsstem" indexed="true" stored="true" multiValued="false"/>
<field name="equipment" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="location" type="location" indexed="true" stored="true" multiValued="false"/>
<field name="comments" type="terms_jsstem" indexed="true" stored="true" multiValued="false"/>
<field name="job" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="starttime" type="string" indexed="false" stored="true" multiValued="false"/>
<field name="endtime" type="string" indexed="false" stored="true" multiValued="false"/>

<field name="contenttitle" type="terms_jsstem" indexed="true" stored="true" multiValued="false"/>
<field name="contentbody" type="terms_jsstem" indexed="true" stored="true" multiValued="false"/>
<field name="contentall" type="terms_jsstem" indexed="true" stored="true" multiValued="false"/>


<copyField source="grammars" dest="grammars_zen"/>
<copyField source="contenttitle" dest="contentall"/>
<copyField source="contentbody" dest="contentall"/>

 <dynamicField name="*_i"  type="int"    indexed="true"  stored="true"/>
   <dynamicField name="*_is" type="int"    indexed="true"  stored="true"  multiValued="true"/>
   <dynamicField name="*_s"  type="string"  indexed="true"  stored="true" />
   <dynamicField name="*_ss" type="string"  indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*_l"  type="long"   indexed="true"  stored="true"/>
   <dynamicField name="*_ls" type="long"   indexed="true"  stored="true"  multiValued="true"/>
   <dynamicField name="*_t"  type="text_general"    indexed="true"  stored="true"/>
   <dynamicField name="*_txt" type="text_general"   indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*_en"  type="text_en"    indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*_b"  type="boolean" indexed="true" stored="true"/>
   <dynamicField name="*_bs" type="boolean" indexed="true" stored="true"  multiValued="true"/>
   <dynamicField name="*_f"  type="float"  indexed="true"  stored="true"/>
   <dynamicField name="*_fs" type="float"  indexed="true"  stored="true"  multiValued="true"/>
   <dynamicField name="*_d"  type="double" indexed="true"  stored="true"/>
   <dynamicField name="*_ds" type="double" indexed="true"  stored="true"  multiValued="true"/>

   <!-- Type used to index the lat and lon components for the "location" FieldType -->
   <dynamicField name="*_coordinate"  type="tdouble" indexed="true"  stored="false" />

   <dynamicField name="*_dt"  type="date"    indexed="true"  stored="true"/>
   <dynamicField name="*_dts" type="date"    indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*_p"  type="location" indexed="true" stored="true"/>

   <!-- some trie-coded dynamic fields for faster range queries -->
   <dynamicField name="*_ti" type="tint"    indexed="true"  stored="true"/>
   <dynamicField name="*_tl" type="tlong"   indexed="true"  stored="true"/>
   <dynamicField name="*_tf" type="tfloat"  indexed="true"  stored="true"/>
   <dynamicField name="*_td" type="tdouble" indexed="true"  stored="true"/>
   <dynamicField name="*_tdt" type="tdate"  indexed="true"  stored="true"/>

   <dynamicField name="*_c"   type="currency" indexed="true"  stored="true"/>

   <dynamicField name="ignored_*" type="ignored" multiValued="true"/>
   <dynamicField name="attr_*" type="text_general" indexed="true" stored="true" multiValued="true"/>

   <dynamicField name="random_*" type="random" />